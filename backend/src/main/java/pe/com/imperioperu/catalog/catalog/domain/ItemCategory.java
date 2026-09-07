package pe.com.imperioperu.catalog.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "item_category")
public class ItemCategory {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "book_id") private Book book;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "category_id") private Category category;
    @Column(name = "is_primary") private boolean primary;
    @Column(name = "sort_order") private int sortOrder;
    private boolean active = true;
    @Column(name = "created_at") private Instant createdAt;

    protected ItemCategory() {}
    public ItemCategory(Book book, Category category, boolean primary, int sortOrder) {
        this.book = book;
        this.category = category;
        this.primary = primary;
        this.sortOrder = sortOrder;
        this.createdAt = Instant.now();
    }
    public UUID getId() { return id; }
    public Book getBook() { return book; }
    public Category getCategory() { return category; }
    public boolean isPrimary() { return primary; }
    public int getSortOrder() { return sortOrder; }
    public boolean isActive() { return active; }
}

