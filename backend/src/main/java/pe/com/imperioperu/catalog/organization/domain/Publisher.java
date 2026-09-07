package pe.com.imperioperu.catalog.organization.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "publisher")
public class Publisher {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "organization_id") private Organization organization;
    private String slug;
    private String description;
    private boolean active;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "updated_at") private Instant updatedAt;

    protected Publisher() {}
    public Publisher(Organization organization, String slug, String description) {
        this.organization = organization;
        this.slug = slug;
        this.description = description;
        this.active = true;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
    public UUID getId() { return id; }
    public Organization getOrganization() { return organization; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }

    public void update(String slug, String description, boolean active) {
        this.slug = slug;
        this.description = description;
        this.active = active;
        this.updatedAt = Instant.now();
    }
}
