package pe.com.imperioperu.catalog.catalog.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import pe.com.imperioperu.catalog.seo.domain.SeoMetadata;

@Entity
@Table(name = "category")
public class Category {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "parent_id") private Category parent;
    @Enumerated(EnumType.STRING) @Column(name = "root_type") private RootType rootType;
    private String code;
    @Column(name = "display_name") private String displayName;
    @Column(name = "slug_candidate") private String slugCandidate;
    @Column(name = "public_path") private String publicPath;
    private String description;
    @Column(name = "sort_order") private int sortOrder;
    private boolean active;
    private boolean visible;
    @Column(name = "show_in_menu") private boolean showInMenu;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) @JoinColumn(name = "seo_metadata_id") private SeoMetadata seoMetadata;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "updated_at") private Instant updatedAt;
    @Column(name = "created_by") private String createdBy;
    @Column(name = "updated_by") private String updatedBy;

    protected Category() {}
    public Category(RootType rootType, String code, String displayName) {
        this.rootType = rootType;
        this.code = code;
        this.displayName = displayName;
        this.active = true;
        this.visible = true;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(Category parent, RootType rootType, String code, String displayName, String slugCandidate,
                       String publicPath, String description, int sortOrder, boolean active, boolean visible,
                       boolean showInMenu, SeoMetadata seoMetadata) {
        if (parent != null && id != null && id.equals(parent.id)) throw new IllegalArgumentException("A category cannot be its own parent");
        if (parent != null && parent.rootType != rootType) throw new IllegalArgumentException("Parent and child must share rootType");
        this.parent = parent;
        this.rootType = rootType;
        this.code = required(code, "code");
        this.displayName = required(displayName, "displayName");
        this.slugCandidate = blankToNull(slugCandidate);
        this.publicPath = blankToNull(publicPath);
        this.description = blankToNull(description);
        this.sortOrder = sortOrder;
        this.active = active;
        this.visible = visible;
        this.showInMenu = showInMenu;
        this.seoMetadata = seoMetadata;
        this.updatedAt = Instant.now();
    }
    public void deactivate() {
        this.active = false;
        this.visible = false;
        this.showInMenu = false;
        this.updatedAt = Instant.now();
    }
    private String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value.trim();
    }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }

    public UUID getId() { return id; }
    public Category getParent() { return parent; }
    public RootType getRootType() { return rootType; }
    public String getCode() { return code; }
    public String getDisplayName() { return displayName; }
    public String getSlugCandidate() { return slugCandidate; }
    public String getPublicPath() { return publicPath; }
    public String getDescription() { return description; }
    public int getSortOrder() { return sortOrder; }
    public boolean isActive() { return active; }
    public boolean isVisible() { return visible; }
    public boolean isShowInMenu() { return showInMenu; }
    public SeoMetadata getSeoMetadata() { return seoMetadata; }
}
