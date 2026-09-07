package pe.com.imperioperu.catalog.inventory.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import pe.com.imperioperu.catalog.catalog.domain.BookOffer;

@Entity
@Table(name = "inventory_item")
public class InventoryItem {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "book_offer_id") private BookOffer bookOffer;
    @Column(name = "on_hand") private int onHand;
    private int reserved;
    @Column(name = "minimum_stock") private int minimumStock;
    @Version private long version;
    @Column(name = "updated_at") private Instant updatedAt;

    protected InventoryItem() {}
    public InventoryItem(BookOffer bookOffer, int onHand, int minimumStock) {
        if (onHand < 0 || minimumStock < 0) throw new IllegalArgumentException("Inventory cannot be negative");
        this.bookOffer = bookOffer;
        this.onHand = onHand;
        this.minimumStock = minimumStock;
        this.updatedAt = Instant.now();
    }

    public void adjustOnHand(int delta) {
        if (onHand + delta < reserved) throw new IllegalArgumentException("Adjustment would reduce stock below reservations");
        onHand += delta;
        touch();
    }
    public void reserve(int quantity) {
        requirePositive(quantity);
        if (available() < quantity) throw new IllegalArgumentException("Insufficient available stock");
        reserved += quantity;
        touch();
    }
    public void release(int quantity) {
        requirePositive(quantity);
        if (reserved < quantity) throw new IllegalArgumentException("Cannot release more than reserved stock");
        reserved -= quantity;
        touch();
    }
    public void sellReserved(int quantity) {
        requirePositive(quantity);
        if (reserved < quantity) throw new IllegalArgumentException("Sale exceeds reserved stock");
        reserved -= quantity;
        onHand -= quantity;
        touch();
    }
    public void setMinimumStock(int minimumStock) {
        if (minimumStock < 0) throw new IllegalArgumentException("Minimum stock cannot be negative");
        this.minimumStock = minimumStock;
        touch();
    }
    public int available() { return onHand - reserved; }
    public InventoryStatus status() {
        if (available() == 0) return InventoryStatus.OUT_OF_STOCK;
        if (available() <= minimumStock) return InventoryStatus.LOW_STOCK;
        return InventoryStatus.IN_STOCK;
    }
    private void requirePositive(int quantity) { if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive"); }
    private void touch() { updatedAt = Instant.now(); }

    public UUID getId() { return id; }
    public BookOffer getBookOffer() { return bookOffer; }
    public int getOnHand() { return onHand; }
    public int getReserved() { return reserved; }
    public int getMinimumStock() { return minimumStock; }
    public long getVersion() { return version; }
}

