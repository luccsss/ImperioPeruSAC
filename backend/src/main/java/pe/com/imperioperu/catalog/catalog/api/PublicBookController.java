package pe.com.imperioperu.catalog.catalog.api;

import static pe.com.imperioperu.catalog.catalog.api.CatalogDtos.BookResponse;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.com.imperioperu.catalog.catalog.application.BookCatalogService;

@RestController
@RequestMapping("/api/v1/public/books")
public class PublicBookController {
    private final BookCatalogService service;
    public PublicBookController(BookCatalogService service) { this.service = service; }

    @GetMapping
    List<BookResponse> published(@RequestParam(defaultValue = "12") int limit) { return service.published(limit); }

    @GetMapping("/{slug}")
    BookResponse bySlug(@PathVariable String slug) { return service.bySlug(slug); }
}

