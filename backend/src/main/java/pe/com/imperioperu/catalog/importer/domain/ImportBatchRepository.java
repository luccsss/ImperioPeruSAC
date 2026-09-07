package pe.com.imperioperu.catalog.importer.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportBatchRepository extends JpaRepository<ImportBatch, UUID> {
    List<ImportBatch> findTop50ByOrderByCreatedAtDesc();
}

