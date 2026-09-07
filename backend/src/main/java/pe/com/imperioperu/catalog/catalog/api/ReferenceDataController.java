package pe.com.imperioperu.catalog.catalog.api;

import static pe.com.imperioperu.catalog.catalog.api.CatalogDtos.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.com.imperioperu.catalog.catalog.domain.Author;
import pe.com.imperioperu.catalog.catalog.domain.AuthorRepository;
import pe.com.imperioperu.catalog.organization.domain.Organization;
import pe.com.imperioperu.catalog.organization.domain.OrganizationRepository;
import pe.com.imperioperu.catalog.organization.domain.OrganizationRole;
import pe.com.imperioperu.catalog.organization.domain.Publisher;
import pe.com.imperioperu.catalog.organization.domain.PublisherRepository;

@RestController
@RequestMapping("/api/v1/admin/reference")
@PreAuthorize("hasAuthority('BOOK_WRITE')")
public class ReferenceDataController {
    private final AuthorRepository authors;
    private final PublisherRepository publishers;
    private final OrganizationRepository organizations;

    public ReferenceDataController(AuthorRepository authors, PublisherRepository publishers, OrganizationRepository organizations) {
        this.authors = authors;
        this.publishers = publishers;
        this.organizations = organizations;
    }

    @GetMapping("/authors") @Transactional(readOnly = true)
    List<AuthorDto> authors() { return authors.findAllByOrderByNameAsc().stream().map(this::author).toList(); }

    @PostMapping("/authors") @Transactional
    ResponseEntity<AuthorDto> createAuthor(@Valid @RequestBody AuthorRequest request) {
        authors.findBySlug(request.slug()).ifPresent(value -> { throw new IllegalArgumentException("Author slug already exists"); });
        var saved = authors.save(new Author(request.name(), request.slug(), request.biography()));
        saved.update(request.name(), request.slug(), request.biography(), request.active());
        return ResponseEntity.created(URI.create("/api/v1/admin/reference/authors/" + saved.getId())).body(author(saved));
    }

    @PutMapping("/authors/{id}") @Transactional
    AuthorDto updateAuthor(@PathVariable UUID id, @Valid @RequestBody AuthorRequest request) {
        var value = authors.findById(id).orElseThrow(() -> new EntityNotFoundException("Author not found"));
        authors.findBySlug(request.slug()).filter(other -> !other.getId().equals(id)).ifPresent(other -> { throw new IllegalArgumentException("Author slug already exists"); });
        value.update(request.name(), request.slug(), request.biography(), request.active());
        return author(value);
    }

    @GetMapping("/publishers") @Transactional(readOnly = true)
    List<PublisherDto> publishers() { return publishers.findAll().stream().map(this::publisher).toList(); }

    @PostMapping("/publishers") @Transactional
    ResponseEntity<PublisherDto> createPublisher(@Valid @RequestBody PublisherRequest request) {
        var organization = organizations.save(new Organization(request.legalName(), request.tradeName(), request.taxId(), EnumSet.of(OrganizationRole.PUBLISHER)));
        var saved = publishers.save(new Publisher(organization, request.slug(), request.description()));
        saved.update(request.slug(), request.description(), request.active());
        return ResponseEntity.created(URI.create("/api/v1/admin/reference/publishers/" + saved.getId())).body(publisher(saved));
    }

    @PutMapping("/publishers/{id}") @Transactional
    PublisherDto updatePublisher(@PathVariable UUID id, @Valid @RequestBody PublisherRequest request) {
        var value = publishers.findById(id).orElseThrow(() -> new EntityNotFoundException("Publisher not found"));
        value.getOrganization().update(request.legalName(), request.tradeName(), request.taxId(), request.active(), EnumSet.of(OrganizationRole.PUBLISHER));
        value.update(request.slug(), request.description(), request.active());
        return publisher(value);
    }

    private AuthorDto author(Author value) { return new AuthorDto(value.getId(), value.getName(), value.getSlug(), value.getBiography(), value.isActive()); }
    private PublisherDto publisher(Publisher value) { return new PublisherDto(value.getId(), value.getOrganization().getId(), value.getOrganization().getTradeName(), value.getSlug(), value.getDescription(), value.isActive()); }
}

