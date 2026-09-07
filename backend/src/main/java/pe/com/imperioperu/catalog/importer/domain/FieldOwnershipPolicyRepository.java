package pe.com.imperioperu.catalog.importer.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldOwnershipPolicyRepository extends JpaRepository<FieldOwnershipPolicy, UUID> {
    List<FieldOwnershipPolicy> findByCatalogSourceIdAndActiveTrue(UUID catalogSourceId);
}

