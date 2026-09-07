package pe.com.imperioperu.catalog.inventory.domain;

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

@Entity
@Table(name = "inventory_movement")
public class InventoryMovement {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "inventory_item_id") private InventoryItem inventoryItem;
    @Enumerated(EnumType.STRING) @Column(name = "movement_type") private InventoryMovementType movementType;
    private int quantity;
    @Column(name = "on_hand_after") private int onHandAfter;
    @Column(name = "reserved_after") private int reservedAfter;
    private String reason;
    @Column(name = "reference_type") private String referenceType;
    @Column(name = "reference_id") private String referenceId;
    private String actor;
    @Column(name = "occurred_at") private Instant occurredAt;

    protected InventoryMovement() {}
    public InventoryMovement(InventoryItem item, InventoryMovementType type, int quantity, String reason,
                             String referenceType, String referenceId, String actor) {
        this.inventoryItem = item;
        this.movementType = type;
        this.quantity = quantity;
        this.onHandAfter = item.getOnHand();
        this.reservedAfter = item.getReserved();
        this.reason = reason;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.actor = actor;
        this.occurredAt = Instant.now();
    }
    public UUID getId() { return id; }
    public InventoryMovementType getMovementType() { return movementType; }
    public int getQuantity() { return quantity; }
    public int getOnHandAfter() { return onHandAfter; }
    public int getReservedAfter() { return reservedAfter; }
    public String getReason() { return reason; }
    public String getActor() { return actor; }
    public Instant getOccurredAt() { return occurredAt; }
}

