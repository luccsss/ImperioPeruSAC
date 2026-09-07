package pe.com.imperioperu.catalog.seo.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RedirectRuleRepository extends JpaRepository<RedirectRule, UUID> {
    Optional<RedirectRule> findBySourcePathAndActiveTrue(String sourcePath);
}

