package pe.com.imperioperu.catalog.catalog.domain;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCategoryRepository extends JpaRepository<ItemCategory, UUID> {
    @EntityGraph(attributePaths = {"category", "category.parent"})
    List<ItemCategory> findByBookIdAndActiveTrueOrderBySortOrderAsc(UUID bookId);
    void deleteByBookId(UUID bookId);
    long countByBookIdAndPrimaryTrueAndActiveTrue(UUID bookId);
}

