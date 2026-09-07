package pe.com.imperioperu.catalog.seo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "redirect_rule")
public class RedirectRule {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @Column(name = "source_path") private String sourcePath;
    @Column(name = "destination_path") private String destinationPath;
    @Column(name = "status_code") private int statusCode;
    private boolean active;
    private String reason;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "created_by") private String createdBy;

    protected RedirectRule() {}
    public RedirectRule(String sourcePath, String destinationPath, int statusCode, String reason, String createdBy) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
        this.statusCode = statusCode;
        this.reason = reason;
        this.createdBy = createdBy;
        this.active = true;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getSourcePath() { return sourcePath; }
    public String getDestinationPath() { return destinationPath; }
    public int getStatusCode() { return statusCode; }
    public boolean isActive() { return active; }
}
