package pe.com.imperioperu.catalog.media.api;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pe.com.imperioperu.catalog.audit.application.AuditService;
import pe.com.imperioperu.catalog.catalog.api.CatalogDtos.ImageDto;
import pe.com.imperioperu.catalog.catalog.domain.BookImage;
import pe.com.imperioperu.catalog.catalog.domain.BookImageRepository;
import pe.com.imperioperu.catalog.catalog.domain.BookRepository;
import pe.com.imperioperu.catalog.catalog.domain.ImageType;
import pe.com.imperioperu.catalog.media.application.MediaStorageService;
import pe.com.imperioperu.catalog.organization.domain.OrganizationRepository;

@RestController
@Validated
public class BookImageController {
    private final MediaStorageService storage;
    private final BookRepository books;
    private final BookImageRepository images;
    private final OrganizationRepository organizations;
    private final AuditService auditService;

    public BookImageController(MediaStorageService storage, BookRepository books, BookImageRepository images,
                               OrganizationRepository organizations, AuditService auditService) {
        this.storage = storage;
        this.books = books;
        this.images = images;
        this.organizations = organizations;
        this.auditService = auditService;
    }

    @PostMapping(path = "/api/v1/admin/books/{bookId}/images", consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('BOOK_WRITE')")
    @Transactional
    ImageDto upload(@PathVariable UUID bookId,
                    @RequestPart("file") MultipartFile file,
                    @RequestParam @NotNull ImageType type,
                    @RequestParam @NotBlank String altText,
                    @RequestParam @Min(1) @Max(12000) int width,
                    @RequestParam @Min(1) @Max(12000) int height,
                    @RequestParam(defaultValue = "0") @Min(0) int sortOrder,
                    @RequestParam(defaultValue = "false") boolean authorized,
                    @RequestParam(required = false) UUID sourceOrganizationId) throws IOException {
        var book = books.findById(bookId).orElseThrow(() -> new EntityNotFoundException("Book not found"));
        var source = sourceOrganizationId == null ? null : organizations.findById(sourceOrganizationId)
            .orElseThrow(() -> new EntityNotFoundException("Source organization not found"));
        if (source != null && !authorized) throw new IllegalArgumentException("Externally sourced media must be explicitly authorized");
        var stored = storage.store(file);
        var image = images.save(new BookImage(book, type, stored.storageKey(), altText.trim(), stored.mediaType(), width, height, sortOrder, authorized, source));
        auditService.record("BOOK_IMAGE_UPLOADED", "BookImage", image.getId(), null, java.util.Map.of("bookId", bookId, "type", type, "authorized", authorized));
        return new ImageDto(image.getId(), image.getImageType().name(), "/media/" + image.getStorageKey(), image.getAltText(), image.getWidth(), image.getHeight(), image.getSortOrder(), image.isAuthorized());
    }
}

