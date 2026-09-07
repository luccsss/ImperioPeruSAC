package pe.com.imperioperu.catalog.inventory.api;

import static pe.com.imperioperu.catalog.catalog.api.CatalogDtos.InventoryAdjustmentRequest;
import static pe.com.imperioperu.catalog.catalog.api.CatalogDtos.InventoryDto;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.imperioperu.catalog.inventory.application.InventoryService;
import pe.com.imperioperu.catalog.inventory.domain.InventoryItem;

@RestController
@RequestMapping("/api/v1/admin/inventory")
public class InventoryController {
    private final InventoryService service;
    public InventoryController(InventoryService service) { this.service = service; }

    @GetMapping("/book/{bookId}") @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    InventoryDto byBook(@PathVariable UUID bookId) { return dto(service.byBook(bookId)); }

    @PostMapping("/{inventoryId}/adjust") @PreAuthorize("hasAuthority('INVENTORY_ADJUST')")
    InventoryDto adjust(@PathVariable UUID inventoryId, @Valid @RequestBody InventoryAdjustmentRequest request) {
        return dto(service.adjust(inventoryId, request.delta(), request.minimumStock(), request.reason()));
    }

    @GetMapping("/{inventoryId}/history") @PreAuthorize("hasAuthority('INVENTORY_VIEW')")
    List<MovementResponse> history(@PathVariable UUID inventoryId) {
        return service.history(inventoryId).stream().map(value -> new MovementResponse(value.getId(), value.getMovementType().name(), value.getQuantity(),
            value.getOnHandAfter(), value.getReservedAfter(), value.getReason(), value.getActor(), value.getOccurredAt())).toList();
    }

    private InventoryDto dto(InventoryItem value) { return new InventoryDto(value.getId(), value.getOnHand(), value.getReserved(), value.available(), value.getMinimumStock(), value.status().name(), value.getVersion()); }
    record MovementResponse(UUID id, String type, int quantity, int onHandAfter, int reservedAfter, String reason, String actor, Instant occurredAt) {}
}

