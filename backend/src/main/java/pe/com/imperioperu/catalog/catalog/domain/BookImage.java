package pe.com.imperioperu.catalog.catalog.domain;

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
import pe.com.imperioperu.catalog.organization.domain.Organization;

@Entity
@Table(name = "book_image")
public class BookImage {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "book_id") private Book book;
    @Enumerated(EnumType.STRING) @Column(name = "image_type") private ImageType imageType;
    @Column(name = "storage_key") private String storageKey;
    @Column(name = "alt_text") private String altText;
    @Column(name = "media_type") private String mediaType;
    private Integer width;
    private Integer height;
    @Column(name = "sort_order") private int sortOrder;
    private boolean authorized;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "source_organization_id") private Organization sourceOrganization;
    @Column(name = "created_at") private Instant createdAt;

    protected BookImage() {}
    public BookImage(Book book, ImageType imageType, String storageKey, String altText, String mediaType,
                     Integer width, Integer height, int sortOrder, boolean authorized, Organization sourceOrganization) {
        this.book = book;
        this.imageType = imageType;
        this.storageKey = storageKey;
        this.altText = altText;
        this.mediaType = mediaType;
        this.width = width;
        this.height = height;
        this.sortOrder = sortOrder;
        this.authorized = authorized;
        this.sourceOrganization = sourceOrganization;
        this.createdAt = Instant.now();
    }
    public UUID getId() { return id; }
    public ImageType getImageType() { return imageType; }
    public String getStorageKey() { return storageKey; }
    public String getAltText() { return altText; }
    public String getMediaType() { return mediaType; }
    public Integer getWidth() { return width; }
    public Integer getHeight() { return height; }
    public int getSortOrder() { return sortOrder; }
    public boolean isAuthorized() { return authorized; }
}

