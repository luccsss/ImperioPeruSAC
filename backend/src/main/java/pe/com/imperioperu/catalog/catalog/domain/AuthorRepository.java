package pe.com.imperioperu.catalog.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Optional<Author> findBySlug(String slug);
    List<Author> findAllByOrderByNameAsc();
}

