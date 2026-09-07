package pe.com.imperioperu.catalog.catalog.api;

import static pe.com.imperioperu.catalog.catalog.api.CatalogDtos.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.imperioperu.catalog.catalog.application.CategoryService;

@RestController
public class CategoryController {
    private final CategoryService service;
    public CategoryController(CategoryService service) { this.service = service; }

    @GetMapping("/api/v1/public/navigation")
    List<NavigationNode> navigation() { return service.navigation(); }

    @GetMapping("/api/v1/admin/categories") @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    List<CategoryResponse> list() { return service.list(); }

    @PostMapping("/api/v1/admin/categories") @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        var value = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/categories/" + value.id())).body(value);
    }

    @PutMapping("/api/v1/admin/categories/{id}") @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    CategoryResponse update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) { return service.update(id, request); }

    @DeleteMapping("/api/v1/admin/categories/{id}") @PreAuthorize("hasAuthority('CATEGORY_MANAGE')")
    CategoryResponse deactivate(@PathVariable UUID id) { return service.deactivate(id); }
}

