package pe.com.imperioperu.catalog.inventory.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    List<InventoryMovement> findTop100ByInventoryItemIdOrderByOccurredAtDesc(UUID inventoryItemId);
}

