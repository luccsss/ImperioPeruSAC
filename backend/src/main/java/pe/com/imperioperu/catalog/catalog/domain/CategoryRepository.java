package pe.com.imperioperu.catalog.catalog.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByCode(String code);
    boolean existsByParentId(UUID parentId);

    @EntityGraph(attributePaths = {"parent", "seoMetadata"})
    List<Category> findAllByOrderByRootTypeAscSortOrderAscDisplayNameAsc();

    List<Category> findByActiveTrueAndVisibleTrueAndShowInMenuTrueOrderByRootTypeAscSortOrderAscDisplayNameAsc();
}

