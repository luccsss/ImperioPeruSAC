package pe.com.imperioperu.catalog.importer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "import_batch")
public class ImportBatch {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "catalog_source_id") private CatalogSource catalogSource;
    @Column(name = "original_filename") private String originalFilename;
    @Column(name = "content_type") private String contentType;
    @Column(name = "checksum_sha256", length = 64) private String checksumSha256;
    @Enumerated(EnumType.STRING) private ImportBatchStatus status;
    @Column(name = "total_rows") private int totalRows;
    @Column(name = "valid_rows") private int validRows;
    @Column(name = "error_rows") private int errorRows;
    @Column(name = "created_by") private String createdBy;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "applied_at") private Instant appliedAt;

    protected ImportBatch() {}
    public ImportBatch(CatalogSource source, String filename, String contentType, String checksum, String actor) {
        this.catalogSource = source;
        this.originalFilename = filename;
        this.contentType = contentType;
        this.checksumSha256 = checksum;
        this.status = ImportBatchStatus.PREVIEWED;
        this.createdBy = actor;
        this.createdAt = Instant.now();
    }
    public void summarize(int total, int valid, int errors, boolean reviewRequired) {
        this.totalRows = total;
        this.validRows = valid;
        this.errorRows = errors;
        this.status = reviewRequired ? ImportBatchStatus.REVIEW_REQUIRED : ImportBatchStatus.PREVIEWED;
    }
    public void approve() { this.status = ImportBatchStatus.APPROVED; }
    public void applied() { this.status = ImportBatchStatus.APPLIED; this.appliedAt = Instant.now(); }
    public void fail() { this.status = ImportBatchStatus.FAILED; }
    public UUID getId() { return id; }
    public CatalogSource getCatalogSource() { return catalogSource; }
    public String getOriginalFilename() { return originalFilename; }
    public String getChecksumSha256() { return checksumSha256; }
    public ImportBatchStatus getStatus() { return status; }
    public int getTotalRows() { return totalRows; }
    public int getValidRows() { return validRows; }
    public int getErrorRows() { return errorRows; }
    public Instant getCreatedAt() { return createdAt; }
}
