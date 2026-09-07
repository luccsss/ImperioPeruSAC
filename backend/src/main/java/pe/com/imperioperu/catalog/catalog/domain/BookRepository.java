package pe.com.imperioperu.catalog.catalog.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findByIsbn13(String isbn13);
    Optional<Book> findByIsbn10(String isbn10);

    @EntityGraph(attributePaths = {"offer", "authors", "publisher", "publisher.organization", "seoMetadata"})
    Optional<Book> findOneByOfferSlug(String slug);

    @EntityGraph(attributePaths = {"offer", "authors", "publisher", "publisher.organization", "seoMetadata"})
    Optional<Book> findOneByOfferSlugAndOfferStatus(String slug, BookStatus status);
}
