package pe.com.imperioperu.catalog.organization.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "organization")
public class Organization {
    @Id private UUID id;
    @Column(name = "legal_name") private String legalName;
    @Column(name = "trade_name") private String tradeName;
    @Column(name = "tax_id") private String taxId;
    private boolean active;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "updated_at") private Instant updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @jakarta.persistence.CollectionTable(name = "organization_role", joinColumns = @jakarta.persistence.JoinColumn(name = "organization_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<OrganizationRole> roles = new LinkedHashSet<>();

    protected Organization() {}
    public Organization(String legalName, String tradeName, String taxId, Set<OrganizationRole> roles) {
        this.id = UUID.randomUUID();
        this.legalName = legalName;
        this.tradeName = tradeName;
        this.taxId = taxId;
        this.active = true;
        this.roles.addAll(roles);
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
    public void update(String legalName, String tradeName, String taxId, boolean active, Set<OrganizationRole> roles) {
        this.legalName = legalName;
        this.tradeName = tradeName;
        this.taxId = taxId;
        this.active = active;
        this.roles.clear();
        this.roles.addAll(roles);
        this.updatedAt = Instant.now();
    }
    public UUID getId() { return id; }
    public String getLegalName() { return legalName; }
    public String getTradeName() { return tradeName; }
    public String getTaxId() { return taxId; }
    public boolean isActive() { return active; }
    public Set<OrganizationRole> getRoles() { return Set.copyOf(roles); }
}
