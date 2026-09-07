package pe.com.imperioperu.catalog.importer.application;

import jakarta.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.com.imperioperu.catalog.audit.application.AuditService;
import pe.com.imperioperu.catalog.catalog.domain.Book;
import pe.com.imperioperu.catalog.catalog.domain.BookOfferRepository;
import pe.com.imperioperu.catalog.catalog.domain.BookRepository;
import pe.com.imperioperu.catalog.importer.domain.CatalogSourceRepository;
import pe.com.imperioperu.catalog.importer.domain.FieldOwnership;
import pe.com.imperioperu.catalog.importer.domain.FieldOwnershipPolicyRepository;
import pe.com.imperioperu.catalog.importer.domain.ImportBatch;
import pe.com.imperioperu.catalog.importer.domain.ImportBatchRepository;
import pe.com.imperioperu.catalog.importer.domain.ImportBatchStatus;
import pe.com.imperioperu.catalog.importer.domain.ImportDisposition;
import pe.com.imperioperu.catalog.importer.domain.ImportRow;
import pe.com.imperioperu.catalog.importer.domain.ImportRowRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
public class ImportPreviewService {
    private static final Set<String> REQUIRED_HEADERS = Set.of("title");
    private final List<CatalogSourceAdapter> adapters;
    private final CatalogSourceRepository sources;
    private final FieldOwnershipPolicyRepository policies;
    private final ImportBatchRepository batches;
    private final ImportRowRepository rows;
    private final BookRepository books;
    private final BookOfferRepository offers;
    private final ObjectMapper objectMapper;
    private final AuditService auditService;
    private final int maxRows;
    private final boolean applyEnabled;

    public ImportPreviewService(List<CatalogSourceAdapter> adapters, CatalogSourceRepository sources,
                                FieldOwnershipPolicyRepository policies, ImportBatchRepository batches,
                                ImportRowRepository rows, BookRepository books, BookOfferRepository offers,
                                ObjectMapper objectMapper, AuditService auditService,
                                @Value("${app.import.max-rows:10000}") int maxRows,
                                @Value("${app.import.apply-enabled:false}") boolean applyEnabled) {
        this.adapters = adapters;
        this.sources = sources;
        this.policies = policies;
        this.batches = batches;
        this.rows = rows;
        this.books = books;
        this.offers = offers;
        this.objectMapper = objectMapper;
        this.auditService = auditService;
        this.maxRows = maxRows;
        this.applyEnabled = applyEnabled;
    }

    @Transactional
    public BatchResponse preview(String sourceCode, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("A non-empty CSV or XLSX file is required");
        String safeName = safeFilename(file.getOriginalFilename());
        byte[] bytes = file.getBytes();
        var source = sources.findByCodeAndActiveTrue(sourceCode).orElseThrow(() -> new EntityNotFoundException("Catalog source not found"));
        var adapter = adapters.stream().filter(value -> value.supports(safeName, file.getContentType())).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Only CSV and XLSX files are supported"));
        var parsed = adapter.read(new ByteArrayInputStream(bytes), maxRows);
        validateHeaders(parsed);
        var batch = batches.save(new ImportBatch(source, safeName, file.getContentType() == null ? "application/octet-stream" : file.getContentType(), sha256(bytes), actor()));
        var sourcePolicies = policies.findByCatalogSourceIdAndActiveTrue(source.getId());
        var ownershipByField = sourcePolicies.stream().collect(java.util.stream.Collectors.toMap(value -> normalize(value.getFieldName()), value -> value.getOwnership()));
        var seenIsbn = new HashSet<String>();
        int valid = 0;
        int errors = 0;
        boolean needsReview = false;
        int rowNumber = 1;
        for (var payload : parsed) {
            rowNumber++;
            var result = analyze(payload, seenIsbn, ownershipByField);
            if (result.disposition == ImportDisposition.INVALID || result.disposition == ImportDisposition.DUPLICATE_IN_FILE) errors++; else valid++;
            if (result.disposition == ImportDisposition.NEEDS_REVIEW) needsReview = true;
            rows.save(new ImportRow(batch, rowNumber, json(payload), result.matchType, result.bookId, result.disposition, json(result.messages), json(result.changes)));
        }
        batch.summarize(parsed.size(), valid, errors, needsReview);
        auditService.record("IMPORT_PREVIEWED", "ImportBatch", batch.getId(), null, Map.of("filename", safeName, "rows", parsed.size(), "checksum", batch.getChecksumSha256()));
        return response(batch, rows.findByBatchIdOrderByRowNumberAsc(batch.getId()));
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> list() { return batches.findTop50ByOrderByCreatedAtDesc().stream().map(value -> response(value, List.of())).toList(); }

    @Transactional(readOnly = true)
    public BatchResponse get(UUID id) {
        var batch = batches.findById(id).orElseThrow(() -> new EntityNotFoundException("Import batch not found"));
        return response(batch, rows.findByBatchIdOrderByRowNumberAsc(id));
    }

    @Transactional
    public BatchResponse decide(UUID batchId, List<RowDecision> decisions) {
        var batch = batches.findById(batchId).orElseThrow(() -> new EntityNotFoundException("Import batch not found"));
        var byId = rows.findByBatchIdOrderByRowNumberAsc(batchId).stream().collect(java.util.stream.Collectors.toMap(ImportRow::getId, value -> value));
        for (var decision : decisions) {
            var row = byId.get(decision.rowId());
            if (row == null) throw new IllegalArgumentException("Row does not belong to batch");
            row.decide(decision.approve(), actor());
        }
        boolean pending = byId.values().stream().anyMatch(value -> value.getDisposition() == ImportDisposition.NEEDS_REVIEW);
        if (!pending) batch.approve();
        auditService.record("IMPORT_DECIDED", "ImportBatch", batchId, null, Map.of("decisions", decisions.size(), "pending", pending));
        return response(batch, byId.values().stream().sorted(java.util.Comparator.comparingInt(ImportRow::getRowNumber)).toList());
    }

    @Transactional
    public BatchResponse apply(UUID batchId) {
        if (!applyEnabled) throw new IllegalStateException("Productive import application is disabled in Phase 2A");
        var batch = batches.findById(batchId).orElseThrow(() -> new EntityNotFoundException("Import batch not found"));
        if (batch.getStatus() != ImportBatchStatus.APPROVED) throw new IllegalArgumentException("Batch must be fully approved before apply");
        var approved = rows.findByBatchIdAndDisposition(batchId, ImportDisposition.APPROVED);
        for (var row : approved) applyBibliographicUpdate(row);
        batch.applied();
        auditService.record("IMPORT_APPLIED", "ImportBatch", batchId, null, Map.of("rows", approved.size()));
        return response(batch, rows.findByBatchIdOrderByRowNumberAsc(batchId));
    }

    private Analysis analyze(Map<String, String> payload, Set<String> seenIsbn, Map<String, FieldOwnership> ownership) {
        var messages = new ArrayList<String>();
        String title = payload.getOrDefault("title", "").trim();
        String isbn13 = digits(payload.get("isbn13"));
        String isbn10 = normalizeIsbn10(payload.get("isbn10"));
        if (title.isBlank()) messages.add("title is required");
        if (!isbn13.isBlank() && isbn13.length() != 13) messages.add("isbn13 must contain 13 digits");
        if (!isbn10.isBlank() && isbn10.length() != 10) messages.add("isbn10 must contain 10 characters");
        String identity = !isbn13.isBlank() ? "13:" + isbn13 : !isbn10.isBlank() ? "10:" + isbn10 : "";
        if (!identity.isBlank() && !seenIsbn.add(identity)) return new Analysis(null, "ISBN", ImportDisposition.DUPLICATE_IN_FILE, List.of("duplicate ISBN in file"), Map.of());
        if (!messages.isEmpty()) return new Analysis(null, null, ImportDisposition.INVALID, messages, Map.of());
        Book match = null;
        String matchType = null;
        if (!isbn13.isBlank()) { match = books.findByIsbn13(isbn13).orElse(null); matchType = match == null ? null : "ISBN13"; }
        if (match == null && !isbn10.isBlank()) { match = books.findByIsbn10(isbn10).orElse(null); matchType = match == null ? null : "ISBN10"; }
        if (match == null && payload.get("sku") != null && !payload.get("sku").isBlank()) {
            match = offers.findBySku(payload.get("sku").trim()).map(value -> value.getBook()).orElse(null);
            matchType = match == null ? null : "SKU";
        }
        if (match == null) return new Analysis(null, null, ImportDisposition.NEW_REQUIRES_COMMERCIAL_DATA,
            List.of("new book requires explicit SKU, USD price, category and publishing review"), Map.of());
        Map<String, Object> changes = differences(match, payload, ownership);
        return new Analysis(match.getId(), matchType, changes.isEmpty() ? ImportDisposition.MATCHED_NO_CHANGES : ImportDisposition.NEEDS_REVIEW,
            List.of(), changes);
    }

    private Map<String, Object> differences(Book book, Map<String, String> payload, Map<String, FieldOwnership> ownership) {
        var changes = new LinkedHashMap<String, Object>();
        compare(changes, "title", book.getTitle(), payload.get("title"), ownership);
        compare(changes, "subtitle", book.getSubtitle(), payload.get("subtitle"), ownership);
        compare(changes, "edition", book.getEdition(), payload.get("edition"), ownership);
        compare(changes, "language", book.getLanguage(), payload.get("language"), ownership);
        compare(changes, "bindingformat", book.getBindingFormat(), payload.get("bindingformat"), ownership);
        compare(changes, "bibliographicdescription", book.getBibliographicDescription(), payload.get("bibliographicdescription"), ownership);
        for (var entry : payload.entrySet()) {
            if (ownership.get(normalize(entry.getKey())) == FieldOwnership.IMPERIO_COMMERCIAL && !entry.getValue().isBlank()) {
                changes.put(entry.getKey(), Map.of("incoming", entry.getValue(), "protected", true, "action", "ADMIN_APPROVAL_REQUIRED"));
            }
        }
        return changes;
    }

    private void compare(Map<String, Object> changes, String field, Object current, String incoming, Map<String, FieldOwnership> ownership) {
        if (incoming == null || incoming.isBlank()) return;
        if (String.valueOf(current == null ? "" : current).equals(incoming)) return;
        boolean protectedField = ownership.get(normalize(field)) == FieldOwnership.IMPERIO_COMMERCIAL;
        changes.put(field, Map.of("current", current == null ? "" : current, "incoming", incoming, "protected", protectedField,
            "action", protectedField ? "ADMIN_APPROVAL_REQUIRED" : "PROPOSE_UPDATE"));
    }

    private void applyBibliographicUpdate(ImportRow row) {
        if (row.getMatchedBookId() == null) return;
        var book = books.findById(row.getMatchedBookId()).orElseThrow(() -> new EntityNotFoundException("Matched book no longer exists"));
        Map<String, String> payload = readMap(row.getSourcePayload());
        book.updateBibliography(value(payload, "isbn13", book.getIsbn13()), value(payload, "isbn10", book.getIsbn10()),
            value(payload, "title", book.getTitle()), value(payload, "subtitle", book.getSubtitle()), value(payload, "edition", book.getEdition()),
            integer(payload.get("publicationyear"), book.getPublicationYear()), integer(payload.get("pages"), book.getPages()),
            value(payload, "language", book.getLanguage()), value(payload, "bindingformat", book.getBindingFormat()),
            value(payload, "bibliographicdescription", book.getBibliographicDescription()), book.getPublisher(), book.getAuthors());
        row.applied();
    }

    private void validateHeaders(List<Map<String, String>> parsed) {
        if (parsed.isEmpty()) throw new IllegalArgumentException("Import file does not contain data rows");
        var headers = parsed.getFirst().keySet();
        var missing = REQUIRED_HEADERS.stream().filter(value -> !headers.contains(value)).toList();
        if (!missing.isEmpty()) throw new IllegalArgumentException("Missing required columns: " + String.join(", ", missing));
    }
    private String safeFilename(String filename) {
        if (filename == null) return "upload";
        String value = java.nio.file.Path.of(filename).getFileName().toString().replaceAll("[^A-Za-z0-9._-]", "_");
        if (value.length() > 200) value = value.substring(value.length() - 200);
        return value;
    }
    private String sha256(byte[] bytes) {
        try { return java.util.HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)); }
        catch (NoSuchAlgorithmException exception) { throw new IllegalStateException(exception); }
    }
    private String json(Object value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (JacksonException exception) { throw new IllegalStateException("Unable to serialize import data", exception); }
    }
    private Map<String, String> readMap(String value) {
        try { return objectMapper.readValue(value, new TypeReference<>() {}); }
        catch (JacksonException exception) { throw new IllegalStateException("Unable to read import payload", exception); }
    }
    private String digits(String value) { return value == null ? "" : value.replaceAll("[^0-9]", ""); }
    private String normalizeIsbn10(String value) { return value == null ? "" : value.replaceAll("[^0-9Xx]", "").toUpperCase(); }
    private String normalize(String value) { return value == null ? "" : value.replaceAll("[^A-Za-z0-9]", "").toLowerCase(); }
    private String actor() { var auth = SecurityContextHolder.getContext().getAuthentication(); return auth == null ? "system" : auth.getName(); }
    private String value(Map<String, String> payload, String key, String fallback) { var value = payload.get(key); return value == null || value.isBlank() ? fallback : value; }
    private Integer integer(String value, Integer fallback) { try { return value == null || value.isBlank() ? fallback : Integer.valueOf(value); } catch (NumberFormatException ignored) { return fallback; } }

    private BatchResponse response(ImportBatch batch, List<ImportRow> batchRows) {
        return new BatchResponse(batch.getId(), batch.getCatalogSource().getCode(), batch.getOriginalFilename(), batch.getChecksumSha256(), batch.getStatus(),
            batch.getTotalRows(), batch.getValidRows(), batch.getErrorRows(), batch.getCreatedAt(),
            batchRows.stream().map(value -> new RowResponse(value.getId(), value.getRowNumber(), value.getMatchType(), value.getMatchedBookId(),
                value.getDisposition(), value.getValidationMessages(), value.getProposedChanges())).toList());
    }

    private record Analysis(UUID bookId, String matchType, ImportDisposition disposition, List<String> messages, Map<String, Object> changes) {}
    public record RowDecision(UUID rowId, boolean approve) {}
    public record RowResponse(UUID id, int rowNumber, String matchType, UUID matchedBookId, ImportDisposition disposition, String validationMessages, String proposedChanges) {}
    public record BatchResponse(UUID id, String sourceCode, String filename, String checksum, ImportBatchStatus status, int totalRows, int validRows, int errorRows, java.time.Instant createdAt, List<RowResponse> rows) {}
}
