package pe.com.imperioperu.catalog.catalog.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
import pe.com.imperioperu.catalog.common.domain.BaseAuditableEntity;
import pe.com.imperioperu.catalog.common.domain.CurrencyCode;
import pe.com.imperioperu.catalog.common.domain.Money;
import pe.com.imperioperu.catalog.organization.domain.Organization;

@Entity
@Table(name = "book_offer")
public class BookOffer extends BaseAuditableEntity {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "book_id") private Book book;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "seller_organization_id") private Organization seller;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "supplier_organization_id") private Organization supplier;
    private String sku;
    private String slug;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "regular_price_amount", precision = 19, scale = 2)),
        @AttributeOverride(name = "currency", column = @Column(name = "regular_price_currency", length = 3))
    })
    private Money regularPrice;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "promotional_price_amount", precision = 19, scale = 2)),
        @AttributeOverride(name = "currency", column = @Column(name = "promotional_price_currency", length = 3))
    })
    private Money promotionalPrice;

    @Column(name = "commercial_description") private String commercialDescription;
    @Enumerated(EnumType.STRING) private BookStatus status = BookStatus.DRAFT;
    private boolean featured;
    @Column(name = "new_arrival") private boolean newArrival;
    @Column(name = "published_at") private Instant publishedAt;
    @Column(name = "archived_at") private Instant archivedAt;

    protected BookOffer() {}

    public BookOffer(Book book, Organization seller, String sku, String slug, Money regularPrice) {
        attachBook(book);
        this.seller = seller;
        updateCommercial(sku, slug, regularPrice, null, null, BookStatus.DRAFT, false, false);
    }

    void attachBook(Book book) { this.book = book; }

    public void updateCommercial(String sku, String slug, Money regularPrice, Money promotionalPrice,
                                 String commercialDescription, BookStatus status, boolean featured, boolean newArrival) {
        if (regularPrice == null || regularPrice.getCurrency() != CurrencyCode.USD) {
            throw new IllegalArgumentException("BOOKS requires an explicit USD regular price");
        }
        if (promotionalPrice != null && promotionalPrice.getCurrency() != CurrencyCode.USD) {
            throw new IllegalArgumentException("BOOKS promotional price must use USD");
        }
        if (promotionalPrice != null && promotionalPrice.getAmount().compareTo(regularPrice.getAmount()) > 0) {
            throw new IllegalArgumentException("Promotional price cannot exceed regular price");
        }
        this.sku = require(sku, "sku");
        this.slug = require(slug, "slug");
        this.regularPrice = regularPrice;
        this.promotionalPrice = promotionalPrice;
        this.commercialDescription = commercialDescription;
        setStatus(status == null ? BookStatus.DRAFT : status);
        this.featured = featured;
        this.newArrival = newArrival;
    }

    public void setSupplier(Organization supplier) { this.supplier = supplier; }
    public void setStatus(BookStatus newStatus) {
        this.status = newStatus;
        if (newStatus == BookStatus.PUBLISHED && publishedAt == null) publishedAt = Instant.now();
        if (newStatus == BookStatus.ARCHIVED || newStatus == BookStatus.DISCONTINUED) archivedAt = Instant.now();
    }

    private String require(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value.trim();
    }

    public UUID getId() { return id; }
    public Book getBook() { return book; }
    public Organization getSeller() { return seller; }
    public Organization getSupplier() { return supplier; }
    public String getSku() { return sku; }
    public String getSlug() { return slug; }
    public Money getRegularPrice() { return regularPrice; }
    public Money getPromotionalPrice() { return promotionalPrice; }
    public String getCommercialDescription() { return commercialDescription; }
    public BookStatus getStatus() { return status; }
    public boolean isFeatured() { return featured; }
    public boolean isNewArrival() { return newArrival; }
    public Instant getPublishedAt() { return publishedAt; }
}

