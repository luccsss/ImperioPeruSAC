package pe.com.imperioperu.catalog.seo.domain;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeoMetadataRepository extends JpaRepository<SeoMetadata, UUID> {}

