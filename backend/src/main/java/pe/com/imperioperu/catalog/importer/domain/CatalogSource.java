package pe.com.imperioperu.catalog.importer.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import pe.com.imperioperu.catalog.organization.domain.Organization;

@Entity
@Table(name = "catalog_source")
public class CatalogSource {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "organization_id") private Organization organization;
    private String code;
    @Column(name = "display_name") private String displayName;
    @Column(name = "source_type") private String sourceType;
    private boolean active;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "updated_at") private Instant updatedAt;

    protected CatalogSource() {}
    public UUID getId() { return id; }
    public Organization getOrganization() { return organization; }
    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public String getSourceType() { return sourceType; }
    public boolean isActive() { return active; }
}

