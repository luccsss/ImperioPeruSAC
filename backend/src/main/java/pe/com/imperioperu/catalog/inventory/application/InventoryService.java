package pe.com.imperioperu.catalog.inventory.application;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.imperioperu.catalog.audit.application.AuditService;
import pe.com.imperioperu.catalog.catalog.domain.BookOffer;
import pe.com.imperioperu.catalog.inventory.domain.InventoryItem;
import pe.com.imperioperu.catalog.inventory.domain.InventoryItemRepository;
import pe.com.imperioperu.catalog.inventory.domain.InventoryMovement;
import pe.com.imperioperu.catalog.inventory.domain.InventoryMovementRepository;
import pe.com.imperioperu.catalog.inventory.domain.InventoryMovementType;

@Service
public class InventoryService {
    private final InventoryItemRepository items;
    private final InventoryMovementRepository movements;
    private final AuditService auditService;

    public InventoryService(InventoryItemRepository items, InventoryMovementRepository movements, AuditService auditService) {
        this.items = items;
        this.movements = movements;
        this.auditService = auditService;
    }

    @Transactional
    public InventoryItem initialize(BookOffer offer, int onHand, int minimumStock) {
        var item = items.save(new InventoryItem(offer, onHand, minimumStock));
        movements.save(new InventoryMovement(item, InventoryMovementType.INITIAL, onHand, "Inventario inicial", "BOOK_OFFER", offer.getId().toString(), actor()));
        return item;
    }

    @Transactional
    public InventoryItem adjust(UUID inventoryId, int delta, int minimumStock, String reason) {
        var item = lock(inventoryId);
        int before = item.getOnHand();
        item.adjustOnHand(delta);
        item.setMinimumStock(minimumStock);
        movements.save(new InventoryMovement(item, InventoryMovementType.ADJUSTMENT, delta, requiredReason(reason), null, null, actor()));
        auditService.record("INVENTORY_ADJUSTED", "InventoryItem", item.getId(),
            java.util.Map.of("onHand", before), java.util.Map.of("onHand", item.getOnHand(), "reserved", item.getReserved()));
        return item;
    }

    @Transactional
    public InventoryItem reserve(UUID inventoryId, int quantity, String referenceId) {
        var item = lock(inventoryId);
        item.reserve(quantity);
        movements.save(new InventoryMovement(item, InventoryMovementType.RESERVATION, quantity, "Reserva", "ORDER", referenceId, actor()));
        return item;
    }

    @Transactional
    public InventoryItem release(UUID inventoryId, int quantity, String referenceId) {
        var item = lock(inventoryId);
        item.release(quantity);
        movements.save(new InventoryMovement(item, InventoryMovementType.RELEASE, -quantity, "Liberación de reserva", "ORDER", referenceId, actor()));
        return item;
    }

    @Transactional(readOnly = true)
    public InventoryItem byBook(UUID bookId) {
        return items.findByBookOfferBookId(bookId).orElseThrow(() -> new EntityNotFoundException("Inventory not found"));
    }

    @Transactional(readOnly = true)
    public List<InventoryMovement> history(UUID inventoryId) {
        return movements.findTop100ByInventoryItemIdOrderByOccurredAtDesc(inventoryId);
    }

    private InventoryItem lock(UUID id) { return items.findLockedById(id).orElseThrow(() -> new EntityNotFoundException("Inventory not found")); }
    private String requiredReason(String reason) {
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("An inventory reason is required");
        return reason.trim();
    }
    private String actor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "system" : auth.getName();
    }
}

