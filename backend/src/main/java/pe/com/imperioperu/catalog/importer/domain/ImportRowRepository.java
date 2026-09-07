package pe.com.imperioperu.catalog.importer.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportRowRepository extends JpaRepository<ImportRow, UUID> {
    List<ImportRow> findByBatchIdOrderByRowNumberAsc(UUID batchId);
    List<ImportRow> findByBatchIdAndDisposition(UUID batchId, ImportDisposition disposition);
}

