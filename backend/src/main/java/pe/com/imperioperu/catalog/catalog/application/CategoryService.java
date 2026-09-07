package pe.com.imperioperu.catalog.catalog.application;

import static pe.com.imperioperu.catalog.catalog.api.CatalogDtos.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.com.imperioperu.catalog.audit.application.AuditService;
import pe.com.imperioperu.catalog.catalog.domain.Category;
import pe.com.imperioperu.catalog.catalog.domain.CategoryRepository;
import pe.com.imperioperu.catalog.seo.domain.RobotsPolicy;
import pe.com.imperioperu.catalog.seo.domain.SeoMetadata;

@Service
public class CategoryService {
    private final CategoryRepository categories;
    private final AuditService auditService;

    public CategoryService(CategoryRepository categories, AuditService auditService) {
        this.categories = categories;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        return categories.findAllByOrderByRootTypeAscSortOrderAscDisplayNameAsc().stream().map(this::response).toList();
    }

    @Transactional(readOnly = true)
    public List<NavigationNode> navigation() {
        var active = categories.findByActiveTrueAndVisibleTrueAndShowInMenuTrueOrderByRootTypeAscSortOrderAscDisplayNameAsc();
        Map<UUID, List<Category>> children = new LinkedHashMap<>();
        for (var category : active) {
            if (category.getParent() != null) children.computeIfAbsent(category.getParent().getId(), ignored -> new ArrayList<>()).add(category);
        }
        return active.stream().filter(value -> value.getParent() == null)
            .map(root -> new NavigationNode(root.getId(), root.getCode(), root.getDisplayName(), href(root), root.getRootType(), root.getSortOrder(),
                children.getOrDefault(root.getId(), List.of()).stream()
                    .sorted(Comparator.comparingInt(Category::getSortOrder).thenComparing(Category::getDisplayName))
                    .map(child -> new NavigationNode(child.getId(), child.getCode(), child.getDisplayName(), href(child), child.getRootType(), child.getSortOrder(), List.of()))
                    .toList()))
            .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        categories.findByCode(request.code()).ifPresent(value -> { throw new IllegalArgumentException("Category code already exists"); });
        var category = new Category(request.rootType(), request.code(), request.displayName());
        apply(category, request);
        category = categories.save(category);
        auditService.record("CATEGORY_CREATED", "Category", category.getId(), null, Map.of("code", category.getCode(), "name", category.getDisplayName()));
        return response(category);
    }

    @Transactional
    public CategoryResponse update(UUID id, CategoryRequest request) {
        var category = get(id);
        categories.findByCode(request.code()).filter(value -> !value.getId().equals(id))
            .ifPresent(value -> { throw new IllegalArgumentException("Category code already exists"); });
        var before = Map.<String, Object>of("name", category.getDisplayName(), "code", category.getCode(), "active", category.isActive());
        apply(category, request);
        auditService.record("CATEGORY_UPDATED", "Category", id, before, Map.of("name", category.getDisplayName(), "code", category.getCode(), "active", category.isActive()));
        return response(category);
    }

    @Transactional
    public CategoryResponse deactivate(UUID id) {
        var category = get(id);
        if (categories.existsByParentId(id)) throw new IllegalArgumentException("Deactivate child categories before deactivating their parent");
        category.deactivate();
        auditService.record("CATEGORY_DEACTIVATED", "Category", id, null, Map.of("active", false));
        return response(category);
    }

    private void apply(Category category, CategoryRequest request) {
        Category parent = request.parentId() == null ? null : get(request.parentId());
        SeoMetadata seo = category.getSeoMetadata();
        if (seo == null) seo = new SeoMetadata(null, null, RobotsPolicy.NOINDEX);
        if (request.seo() != null) {
            var value = request.seo();
            seo.update(value.seoTitle(), value.metaDescription(), value.canonicalUrl(), value.robotsPolicy(), value.ogTitle(), value.ogDescription(), value.ogImageUrl());
        }
        category.update(parent, request.rootType(), request.code(), request.displayName(), request.slugCandidate(), request.publicPath(),
            request.description(), request.sortOrder(), request.active(), request.visible(), request.showInMenu(), seo);
    }

    private Category get(UUID id) { return categories.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found")); }
    private String href(Category value) { return value.getPublicPath() == null ? "/explorar?categoria=" + value.getCode() : value.getPublicPath(); }
    private CategoryResponse response(Category value) {
        return new CategoryResponse(value.getId(), value.getParent() == null ? null : value.getParent().getId(), value.getRootType(), value.getCode(),
            value.getDisplayName(), value.getSlugCandidate(), value.getPublicPath(), href(value), value.getDescription(), value.getSortOrder(),
            value.isActive(), value.isVisible(), value.isShowInMenu(), seo(value.getSeoMetadata()));
    }
    private SeoDto seo(SeoMetadata value) { return value == null ? null : new SeoDto(value.getSeoTitle(), value.getMetaDescription(), value.getCanonicalUrl(), value.getRobotsPolicy(), value.getOgTitle(), value.getOgDescription(), value.getOgImageUrl()); }
}

