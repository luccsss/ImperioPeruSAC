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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.com.imperioperu.catalog.catalog.application.BookCatalogService;

@RestController
@RequestMapping("/api/v1/admin/books")
public class AdminBookController {
    private final BookCatalogService service;
    public AdminBookController(BookCatalogService service) { this.service = service; }

    @GetMapping @PreAuthorize("hasAuthority('BOOK_READ')")
    List<BookResponse> list(@RequestParam(defaultValue = "100") int limit) { return service.listAdmin(limit); }

    @GetMapping("/{id}") @PreAuthorize("hasAuthority('BOOK_READ')")
    BookResponse get(@PathVariable UUID id) { return service.byId(id); }

    @PostMapping @PreAuthorize("hasAuthority('BOOK_WRITE')")
    ResponseEntity<BookResponse> create(@Valid @RequestBody BookUpsertRequest request) {
        var created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/books/" + created.id())).body(created);
    }

    @PutMapping("/{id}") @PreAuthorize("hasAuthority('BOOK_WRITE')")
    BookResponse update(@PathVariable UUID id, @Valid @RequestBody BookUpsertRequest request) { return service.update(id, request); }

    @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('BOOK_ARCHIVE')")
    BookResponse archive(@PathVariable UUID id) { return service.archive(id); }
}

