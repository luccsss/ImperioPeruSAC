package pe.com.imperioperu.catalog.importer.application;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

@Component
public class FileCatalogSourceAdapter implements CatalogSourceAdapter {
    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of("csv", "xlsx");
    private static final Map<String, String> HEADER_ALIASES = Map.ofEntries(
        Map.entry("titulo", "title"),
        Map.entry("subtitulo", "subtitle"),
        Map.entry("autores", "authors"),
        Map.entry("autor", "authors"),
        Map.entry("editorial", "publisher"),
        Map.entry("edicion", "edition"),
        Map.entry("anio", "publicationyear"),
        Map.entry("anopublicacion", "publicationyear"),
        Map.entry("paginas", "pages"),
        Map.entry("idioma", "language"),
        Map.entry("formato", "bindingformat"),
        Map.entry("descripcionbibliografica", "bibliographicdescription"),
        Map.entry("precio", "regularprice"),
        Map.entry("preciopromocional", "promotionalprice"),
        Map.entry("stock", "stock")
    );

    @Override
    public boolean supports(String filename, String contentType) {
        String extension = extension(filename);
        return SUPPORTED_EXTENSIONS.contains(extension);
    }

    @Override
    public List<Map<String, String>> read(InputStream input, int maxRows) throws IOException {
        var buffered = input instanceof BufferedInputStream value ? value : new BufferedInputStream(input);
        buffered.mark(8);
        byte[] signature = buffered.readNBytes(4);
        buffered.reset();
        boolean zip = signature.length == 4 && signature[0] == 'P' && signature[1] == 'K';
        return zip ? readXlsx(buffered, maxRows) : readCsv(buffered, maxRows);
    }

    private List<Map<String, String>> readCsv(InputStream input, int maxRows) throws IOException {
        try (var parser = CSVParser.parse(new InputStreamReader(input, StandardCharsets.UTF_8),
            CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreEmptyLines(true).setTrim(true).get())) {
            var normalizedHeaders = parser.getHeaderNames().stream().map(this::normalizeHeader).toList();
            var rows = new ArrayList<Map<String, String>>();
            for (var record : parser) {
                if (rows.size() >= maxRows) throw new IllegalArgumentException("Import exceeds maximum row count of " + maxRows);
                var row = new LinkedHashMap<String, String>();
                for (int i = 0; i < normalizedHeaders.size(); i++) row.put(normalizedHeaders.get(i), sanitize(record.get(i)));
                rows.add(row);
            }
            return rows;
        }
    }

    private List<Map<String, String>> readXlsx(InputStream input, int maxRows) throws IOException {
        try (var workbook = WorkbookFactory.create(input)) {
            var sheet = workbook.getNumberOfSheets() == 0 ? null : workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() == 0) return List.of();
            var formatter = new DataFormatter(Locale.ROOT);
            var headerRow = sheet.getRow(sheet.getFirstRowNum());
            if (headerRow == null) return List.of();
            var headers = new ArrayList<String>();
            for (int c = 0; c < headerRow.getLastCellNum(); c++) headers.add(normalizeHeader(formatter.formatCellValue(headerRow.getCell(c))));
            var rows = new ArrayList<Map<String, String>>();
            for (int r = headerRow.getRowNum() + 1; r <= sheet.getLastRowNum(); r++) {
                var sourceRow = sheet.getRow(r);
                if (sourceRow == null) continue;
                if (rows.size() >= maxRows) throw new IllegalArgumentException("Import exceeds maximum row count of " + maxRows);
                var row = new LinkedHashMap<String, String>();
                boolean hasValue = false;
                for (int c = 0; c < headers.size(); c++) {
                    var cell = sourceRow.getCell(c);
                    if (cell != null && cell.getCellType() == CellType.FORMULA) throw new IllegalArgumentException("Formula cells are not allowed in imports (row " + (r + 1) + ")");
                    String value = sanitize(formatter.formatCellValue(cell));
                    if (!value.isBlank()) hasValue = true;
                    row.put(headers.get(c), value);
                }
                if (hasValue) rows.add(row);
            }
            return rows;
        }
    }

    private String extension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }
    private String normalizeHeader(String value) {
        if (value == null) return "";
        String normalized = Normalizer.normalize(value.trim().replace("\uFEFF", ""), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .replaceAll("[^A-Za-z0-9]", "")
            .toLowerCase(Locale.ROOT);
        return HEADER_ALIASES.getOrDefault(normalized, normalized);
    }
    private String sanitize(String value) {
        if (value == null) return "";
        String result = value.strip();
        if (result.indexOf('\0') >= 0) throw new IllegalArgumentException("Null bytes are not allowed");
        return result;
    }
}
