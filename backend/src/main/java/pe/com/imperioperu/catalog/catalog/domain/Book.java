package pe.com.imperioperu.catalog.catalog.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import pe.com.imperioperu.catalog.common.domain.BaseAuditableEntity;
import pe.com.imperioperu.catalog.organization.domain.Publisher;
import pe.com.imperioperu.catalog.seo.domain.SeoMetadata;

@Entity
@Table(name = "book")
public class Book extends BaseAuditableEntity {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    private String isbn13;
    private String isbn10;
    private String title;
    private String subtitle;
    private String edition;
    @Column(name = "publication_year") private Integer publicationYear;
    private Integer pages;
    private String language;
    @Column(name = "binding_format") private String bindingFormat;
    @Column(name = "bibliographic_description") private String bibliographicDescription;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "publisher_id") private Publisher publisher;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true) @JoinColumn(name = "seo_metadata_id") private SeoMetadata seoMetadata;

    @ManyToMany
    @JoinTable(name = "book_author", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "author_id"))
    @OrderColumn(name = "sort_order")
    private List<Author> authors = new ArrayList<>();

    @OneToOne(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private BookOffer offer;

    protected Book() {}

    public Book(String title) { this.title = title; }

    public void updateBibliography(String isbn13, String isbn10, String title, String subtitle, String edition,
                                   Integer publicationYear, Integer pages, String language, String bindingFormat,
                                   String bibliographicDescription, Publisher publisher, Collection<Author> authors) {
        this.isbn13 = blankToNull(isbn13);
        this.isbn10 = blankToNull(isbn10);
        this.title = required(title, "title");
        this.subtitle = blankToNull(subtitle);
        this.edition = blankToNull(edition);
        this.publicationYear = publicationYear;
        this.pages = pages;
        this.language = blankToNull(language);
        this.bindingFormat = blankToNull(bindingFormat);
        this.bibliographicDescription = blankToNull(bibliographicDescription);
        this.publisher = publisher;
        this.authors.clear();
        this.authors.addAll(authors);
    }

    public void attachOffer(BookOffer offer) {
        this.offer = offer;
        if (offer != null && offer.getBook() != this) offer.attachBook(this);
    }

    public void attachSeoMetadata(SeoMetadata seoMetadata) { this.seoMetadata = seoMetadata; }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value.trim();
    }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }

    public UUID getId() { return id; }
    public String getIsbn13() { return isbn13; }
    public String getIsbn10() { return isbn10; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getEdition() { return edition; }
    public Integer getPublicationYear() { return publicationYear; }
    public Integer getPages() { return pages; }
    public String getLanguage() { return language; }
    public String getBindingFormat() { return bindingFormat; }
    public String getBibliographicDescription() { return bibliographicDescription; }
    public Publisher getPublisher() { return publisher; }
    public List<Author> getAuthors() { return List.copyOf(authors); }
    public BookOffer getOffer() { return offer; }
    public SeoMetadata getSeoMetadata() { return seoMetadata; }
}

