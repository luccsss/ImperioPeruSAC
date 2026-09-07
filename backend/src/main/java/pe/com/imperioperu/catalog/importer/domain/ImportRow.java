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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "import_row")
public class ImportRow {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "import_batch_id") private ImportBatch batch;
    @Column(name = "row_number") private int rowNumber;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "source_payload", columnDefinition = "jsonb") private String sourcePayload;
    @Column(name = "match_type") private String matchType;
    @Column(name = "matched_book_id") private UUID matchedBookId;
    @Enumerated(EnumType.STRING) private ImportDisposition disposition;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "validation_messages", columnDefinition = "jsonb") private String validationMessages;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "proposed_changes", columnDefinition = "jsonb") private String proposedChanges;
    @Column(name = "decision_by") private String decisionBy;
    @Column(name = "decision_at") private Instant decisionAt;

    protected ImportRow() {}
    public ImportRow(ImportBatch batch, int rowNumber, String payload, String matchType, UUID matchedBookId,
                     ImportDisposition disposition, String validationMessages, String proposedChanges) {
        this.batch = batch;
        this.rowNumber = rowNumber;
        this.sourcePayload = payload;
        this.matchType = matchType;
        this.matchedBookId = matchedBookId;
        this.disposition = disposition;
        this.validationMessages = validationMessages;
        this.proposedChanges = proposedChanges;
    }
    public void decide(boolean approve, String actor) {
        if (disposition != ImportDisposition.NEEDS_REVIEW) throw new IllegalArgumentException("Only review rows can be decided");
        this.disposition = approve ? ImportDisposition.APPROVED : ImportDisposition.REJECTED;
        this.decisionBy = actor;
        this.decisionAt = Instant.now();
    }
    public void applied() { this.disposition = ImportDisposition.APPLIED; }
    public UUID getId() { return id; }
    public int getRowNumber() { return rowNumber; }
    public String getSourcePayload() { return sourcePayload; }
    public String getMatchType() { return matchType; }
    public UUID getMatchedBookId() { return matchedBookId; }
    public ImportDisposition getDisposition() { return disposition; }
    public String getValidationMessages() { return validationMessages; }
    public String getProposedChanges() { return proposedChanges; }
}

