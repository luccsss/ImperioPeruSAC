package pe.com.imperioperu.catalog.catalog.application;

import static pe.com.imperioperu.catalog.catalog.api.CatalogDtos.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.imperioperu.catalog.audit.application.AuditService;
import pe.com.imperioperu.catalog.catalog.domain.Author;
import pe.com.imperioperu.catalog.catalog.domain.AuthorRepository;
import pe.com.imperioperu.catalog.catalog.domain.Book;
import pe.com.imperioperu.catalog.catalog.domain.BookImageRepository;
import pe.com.imperioperu.catalog.catalog.domain.BookOffer;
import pe.com.imperioperu.catalog.catalog.domain.BookOfferRepository;
import pe.com.imperioperu.catalog.catalog.domain.BookRepository;
import pe.com.imperioperu.catalog.catalog.domain.BookStatus;
import pe.com.imperioperu.catalog.catalog.domain.Category;
import pe.com.imperioperu.catalog.catalog.domain.CategoryRepository;
import pe.com.imperioperu.catalog.catalog.domain.ItemCategory;
import pe.com.imperioperu.catalog.catalog.domain.ItemCategoryRepository;
import pe.com.imperioperu.catalog.common.domain.Money;
import pe.com.imperioperu.catalog.inventory.application.InventoryService;
import pe.com.imperioperu.catalog.inventory.domain.InventoryItem;
import pe.com.imperioperu.catalog.inventory.domain.InventoryItemRepository;
import pe.com.imperioperu.catalog.organization.domain.OrganizationRepository;
import pe.com.imperioperu.catalog.organization.domain.Publisher;
import pe.com.imperioperu.catalog.organization.domain.PublisherRepository;
import pe.com.imperioperu.catalog.seo.domain.RedirectRule;
import pe.com.imperioperu.catalog.seo.domain.RedirectRuleRepository;
import pe.com.imperioperu.catalog.seo.domain.RobotsPolicy;
import pe.com.imperioperu.catalog.seo.domain.SeoMetadata;

@Service
public class BookCatalogService {
    private static final UUID IMPERIO_ORGANIZATION_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");
    private final BookRepository books;
    private final BookOfferRepository offers;
    private final AuthorRepository authors;
    private final PublisherRepository publishers;
    private final OrganizationRepository organizations;
    private final CategoryRepository categories;
    private final ItemCategoryRepository itemCategories;
    private final BookImageRepository images;
    private final InventoryItemRepository inventoryItems;
    private final InventoryService inventoryService;
    private final RedirectRuleRepository redirects;
    private final AuditService auditService;

    public BookCatalogService(BookRepository books, BookOfferRepository offers, AuthorRepository authors,
                              PublisherRepository publishers, OrganizationRepository organizations,
                              CategoryRepository categories, ItemCategoryRepository itemCategories,
                              BookImageRepository images, InventoryItemRepository inventoryItems,
                              InventoryService inventoryService, RedirectRuleRepository redirects,
                              AuditService auditService) {
        this.books = books;
        this.offers = offers;
        this.authors = authors;
        this.publishers = publishers;
        this.organizations = organizations;
        this.categories = categories;
        this.itemCategories = itemCategories;
        this.images = images;
        this.inventoryItems = inventoryItems;
        this.inventoryService = inventoryService;
        this.redirects = redirects;
        this.auditService = auditService;
    }

    @Transactional
    public BookResponse create(BookUpsertRequest request) {
        validateIdentityUniqueness(null, request);
        var book = new Book(request.title());
        applyBibliography(book, request);
        var seo = createSeo(request.seo());
        book.attachSeoMetadata(seo);
        var seller = organizations.findById(IMPERIO_ORGANIZATION_ID).orElseThrow(() -> new EntityNotFoundException("Seller organization not found"));
        var offer = new BookOffer(book, seller, request.sku(), request.slug(), money(request.regularPrice()));
        offer.updateCommercial(request.sku(), request.slug(), money(request.regularPrice()), moneyNullable(request.promotionalPrice()),
            request.commercialDescription(), request.status(), request.featured(), request.newArrival());
        book.attachOffer(offer);
        book = books.save(book);
        replaceCategories(book, request.categories());
        inventoryService.initialize(offer, request.initialOnHand(), request.minimumStock());
        auditService.record("BOOK_CREATED", "Book", book.getId(), null, Map.of("title", book.getTitle(), "sku", offer.getSku()));
        return response(book);
    }

    @Transactional
    public BookResponse update(UUID id, BookUpsertRequest request) {
        var book = getBook(id);
        validateIdentityUniqueness(id, request);
        var offer = book.getOffer();
        String previousSlug = offer.getSlug();
        var previousPrice = offer.getRegularPrice();
        applyBibliography(book, request);
        updateSeo(book, request.seo());
        offer.updateCommercial(request.sku(), request.slug(), money(request.regularPrice()), moneyNullable(request.promotionalPrice()),
            request.commercialDescription(), request.status(), request.featured(), request.newArrival());
        replaceCategories(book, request.categories());
        if (!previousSlug.equals(request.slug()) && offer.getPublishedAt() != null) {
            redirects.save(new RedirectRule("/libro/" + previousSlug + "/", "/libro/" + request.slug() + "/", 301,
                "Cambio de slug de libro publicado", actor()));
        }
        if (!previousPrice.equals(offer.getRegularPrice())) {
            auditService.record("BOOK_PRICE_CHANGED", "BookOffer", offer.getId(),
                Map.of("amount", previousPrice.getAmount(), "currency", previousPrice.getCurrency()),
                Map.of("amount", offer.getRegularPrice().getAmount(), "currency", offer.getRegularPrice().getCurrency()));
        }
        auditService.record("BOOK_UPDATED", "Book", book.getId(), null, Map.of("title", book.getTitle(), "status", offer.getStatus()));
        return response(book);
    }

    @Transactional
    public BookResponse archive(UUID id) {
        var book = getBook(id);
        book.getOffer().setStatus(BookStatus.ARCHIVED);
        auditService.record("BOOK_ARCHIVED", "Book", id, null, Map.of("status", "ARCHIVED"));
        return response(book);
    }

    @Transactional(readOnly = true)
    public BookResponse byId(UUID id) { return response(getBook(id)); }

    @Transactional(readOnly = true)
    public BookResponse bySlug(String slug) {
        return response(books.findOneByOfferSlugAndOfferStatus(slug, BookStatus.PUBLISHED)
            .orElseThrow(() -> new EntityNotFoundException("Book not found")));
    }

    @Transactional(readOnly = true)
    public List<BookResponse> listAdmin(int limit) {
        return books.findAll(PageRequest.of(0, Math.min(Math.max(limit, 1), 200))).stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> published(int limit) {
        return offers.findByStatusOrderByUpdatedAtDesc(BookStatus.PUBLISHED, PageRequest.of(0, Math.min(Math.max(limit, 1), 60)))
            .stream().map(BookOffer::getBook).map(this::response).toList();
    }

    private void applyBibliography(Book book, BookUpsertRequest request) {
        Publisher publisher = request.publisherId() == null ? null : publishers.findById(request.publisherId())
            .orElseThrow(() -> new EntityNotFoundException("Publisher not found"));
        List<Author> selectedAuthors = authors.findAllById(request.authorIds());
        if (selectedAuthors.size() != new HashSet<>(request.authorIds()).size()) throw new IllegalArgumentException("One or more authors do not exist");
        book.updateBibliography(request.isbn13(), request.isbn10(), request.title(), request.subtitle(), request.edition(),
            request.publicationYear(), request.pages(), request.language(), request.bindingFormat(), request.bibliographicDescription(), publisher, selectedAuthors);
    }

    private void replaceCategories(Book book, List<CategoryAssignmentRequest> requested) {
        long primaryCount = requested.stream().filter(CategoryAssignmentRequest::primary).count();
        if (primaryCount != 1) throw new IllegalArgumentException("A book must have exactly one primary category");
        var unique = new HashSet<UUID>();
        if (requested.stream().anyMatch(value -> !unique.add(value.categoryId()))) throw new IllegalArgumentException("Category assignments must be unique");
        var categoryById = categories.findAllById(unique).stream().collect(java.util.stream.Collectors.toMap(Category::getId, value -> value));
        if (categoryById.size() != unique.size()) throw new IllegalArgumentException("One or more categories do not exist");
        itemCategories.deleteByBookId(book.getId());
        itemCategories.flush();
        var assignments = requested.stream()
            .map(value -> new ItemCategory(book, categoryById.get(value.categoryId()), value.primary(), value.sortOrder()))
            .toList();
        itemCategories.saveAll(assignments);
    }

    private SeoMetadata createSeo(SeoRequest request) {
        var seo = new SeoMetadata(request == null ? null : request.seoTitle(), request == null ? null : request.metaDescription(),
            request == null || request.robotsPolicy() == null ? RobotsPolicy.NOINDEX : request.robotsPolicy());
        if (request != null) seo.update(request.seoTitle(), request.metaDescription(), request.canonicalUrl(), request.robotsPolicy(), request.ogTitle(), request.ogDescription(), request.ogImageUrl());
        return seo;
    }
    private void updateSeo(Book book, SeoRequest request) {
        if (book.getSeoMetadata() == null) book.attachSeoMetadata(createSeo(request));
        else if (request != null) book.getSeoMetadata().update(request.seoTitle(), request.metaDescription(), request.canonicalUrl(), request.robotsPolicy(), request.ogTitle(), request.ogDescription(), request.ogImageUrl());
    }

    private void validateIdentityUniqueness(UUID bookId, BookUpsertRequest request) {
        if (request.isbn13() != null && !request.isbn13().isBlank()) books.findByIsbn13(request.isbn13()).filter(value -> !value.getId().equals(bookId))
            .ifPresent(value -> { throw new IllegalArgumentException("ISBN-13 already exists"); });
        offers.findBySku(request.sku()).filter(value -> !value.getBook().getId().equals(bookId))
            .ifPresent(value -> { throw new IllegalArgumentException("SKU already exists"); });
        offers.findBySlug(request.slug()).filter(value -> !value.getBook().getId().equals(bookId))
            .ifPresent(value -> { throw new IllegalArgumentException("Slug already exists"); });
    }

    private Book getBook(UUID id) { return books.findById(id).orElseThrow(() -> new EntityNotFoundException("Book not found")); }
    private Money money(MoneyDto value) { return new Money(value.amount(), value.currency()); }
    private Money moneyNullable(MoneyDto value) { return value == null ? null : money(value); }

    private BookResponse response(Book book) {
        var offer = book.getOffer();
        var assigned = itemCategories.findByBookIdAndActiveTrueOrderBySortOrderAsc(book.getId());
        var inventory = inventoryItems.findByBookOfferBookId(book.getId()).map(this::inventoryDto).orElse(null);
        var publisher = book.getPublisher() == null ? null : new PublisherDto(book.getPublisher().getId(), book.getPublisher().getOrganization().getId(),
            book.getPublisher().getOrganization().getTradeName(), book.getPublisher().getSlug(), book.getPublisher().getDescription(), book.getPublisher().isActive());
        return new BookResponse(book.getId(), book.getIsbn13(), book.getIsbn10(), book.getTitle(), book.getSubtitle(), book.getEdition(),
            book.getPublicationYear(), book.getPages(), book.getLanguage(), book.getBindingFormat(), book.getBibliographicDescription(), publisher,
            book.getAuthors().stream().map(value -> new AuthorDto(value.getId(), value.getName(), value.getSlug(), value.getBiography(), value.isActive())).toList(),
            offer.getSku(), offer.getSlug(), "/libro/" + offer.getSlug() + "/", moneyDto(offer.getRegularPrice()), moneyDto(offer.getPromotionalPrice()),
            offer.getCommercialDescription(), offer.getStatus(), offer.isFeatured(), offer.isNewArrival(),
            assigned.stream().map(value -> new CategorySummary(value.getCategory().getId(), value.getCategory().getCode(), value.getCategory().getDisplayName(),
                value.getCategory().getRootType(), value.isPrimary(), categoryHref(value.getCategory()))).toList(), inventory,
            images.findByBookIdOrderBySortOrderAsc(book.getId()).stream().map(value -> new ImageDto(value.getId(), value.getImageType().name(),
                "/media/" + value.getStorageKey(), value.getAltText(), value.getWidth(), value.getHeight(), value.getSortOrder(), value.isAuthorized())).toList(),
            seoDto(book.getSeoMetadata()));
    }

    private String categoryHref(Category category) {
        return category.getPublicPath() != null ? category.getPublicPath() : "/explorar?categoria=" + category.getCode();
    }
    private MoneyDto moneyDto(Money value) { return value == null ? null : new MoneyDto(value.getAmount(), value.getCurrency()); }
    private InventoryDto inventoryDto(InventoryItem value) { return new InventoryDto(value.getId(), value.getOnHand(), value.getReserved(), value.available(), value.getMinimumStock(), value.status().name(), value.getVersion()); }
    private SeoDto seoDto(SeoMetadata value) { return value == null ? null : new SeoDto(value.getSeoTitle(), value.getMetaDescription(), value.getCanonicalUrl(), value.getRobotsPolicy(), value.getOgTitle(), value.getOgDescription(), value.getOgImageUrl()); }
    private String actor() { var auth = SecurityContextHolder.getContext().getAuthentication(); return auth == null ? "system" : auth.getName(); }
}
