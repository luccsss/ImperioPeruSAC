package pe.com.imperioperu.catalog.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookOfferRepository extends JpaRepository<BookOffer, UUID> {
    Optional<BookOffer> findBySku(String sku);
    Optional<BookOffer> findBySlug(String slug);

    @EntityGraph(attributePaths = {"book", "book.authors", "book.publisher", "book.publisher.organization"})
    List<BookOffer> findByStatusOrderByUpdatedAtDesc(BookStatus status, Pageable pageable);
}

