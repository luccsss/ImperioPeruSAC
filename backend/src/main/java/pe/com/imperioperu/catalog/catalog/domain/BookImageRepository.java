package pe.com.imperioperu.catalog.catalog.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImageRepository extends JpaRepository<BookImage, UUID> {
    List<BookImage> findByBookIdOrderBySortOrderAsc(UUID bookId);
}

