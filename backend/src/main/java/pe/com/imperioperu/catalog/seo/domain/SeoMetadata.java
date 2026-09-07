package pe.com.imperioperu.catalog.seo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "seo_metadata")
public class SeoMetadata {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @Column(name = "seo_title") private String seoTitle;
    @Column(name = "meta_description") private String metaDescription;
    @Column(name = "canonical_url") private String canonicalUrl;
    @Enumerated(EnumType.STRING) @Column(name = "robots_policy") private RobotsPolicy robotsPolicy = RobotsPolicy.NOINDEX;
    @Column(name = "og_title") private String ogTitle;
    @Column(name = "og_description") private String ogDescription;
    @Column(name = "og_image_url") private String ogImageUrl;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "updated_at") private Instant updatedAt;

    protected SeoMetadata() {}
    public SeoMetadata(String title, String description, RobotsPolicy robotsPolicy) {
        this.seoTitle = title;
        this.metaDescription = description;
        this.robotsPolicy = robotsPolicy == null ? RobotsPolicy.NOINDEX : robotsPolicy;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void update(String title, String description, String canonicalUrl, RobotsPolicy robotsPolicy, String ogTitle, String ogDescription, String ogImageUrl) {
        this.seoTitle = title;
        this.metaDescription = description;
        this.canonicalUrl = canonicalUrl;
        this.robotsPolicy = robotsPolicy == null ? RobotsPolicy.NOINDEX : robotsPolicy;
        this.ogTitle = ogTitle;
        this.ogDescription = ogDescription;
        this.ogImageUrl = ogImageUrl;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getSeoTitle() { return seoTitle; }
    public String getMetaDescription() { return metaDescription; }
    public String getCanonicalUrl() { return canonicalUrl; }
    public RobotsPolicy getRobotsPolicy() { return robotsPolicy; }
    public String getOgTitle() { return ogTitle; }
    public String getOgDescription() { return ogDescription; }
    public String getOgImageUrl() { return ogImageUrl; }
}

