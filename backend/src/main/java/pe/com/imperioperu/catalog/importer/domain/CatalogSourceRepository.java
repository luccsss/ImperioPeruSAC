package pe.com.imperioperu.catalog.importer.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogSourceRepository extends JpaRepository<CatalogSource, UUID> {
    Optional<CatalogSource> findByCodeAndActiveTrue(String code);
}

