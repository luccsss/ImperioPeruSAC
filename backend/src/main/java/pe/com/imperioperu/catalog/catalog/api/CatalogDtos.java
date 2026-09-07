package pe.com.imperioperu.catalog.catalog.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import pe.com.imperioperu.catalog.catalog.domain.BookStatus;
import pe.com.imperioperu.catalog.catalog.domain.RootType;
import pe.com.imperioperu.catalog.common.domain.CurrencyCode;
import pe.com.imperioperu.catalog.seo.domain.RobotsPolicy;

public final class CatalogDtos {
    private CatalogDtos() {}

    public record MoneyDto(
        @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal amount,
        @NotNull CurrencyCode currency
    ) {}

    public record SeoRequest(
        @Size(max = 180) String seoTitle,
        @Size(max = 320) String metaDescription,
        @Size(max = 500) String canonicalUrl,
        RobotsPolicy robotsPolicy,
        @Size(max = 180) String ogTitle,
        @Size(max = 320) String ogDescription,
        @Size(max = 500) String ogImageUrl
    ) {}

    public record CategoryAssignmentRequest(@NotNull UUID categoryId, boolean primary, @Min(0) int sortOrder) {}

    public record BookUpsertRequest(
        @Pattern(regexp = "^$|^[0-9]{13}$") String isbn13,
        @Pattern(regexp = "^$|^[0-9Xx]{10}$") String isbn10,
        @NotBlank @Size(max = 300) String title,
        @Size(max = 300) String subtitle,
        @Size(max = 100) String edition,
        @Min(1400) @Max(2200) Integer publicationYear,
        @Min(1) Integer pages,
        @Size(max = 30) String language,
        @Size(max = 60) String bindingFormat,
        String bibliographicDescription,
        UUID publisherId,
        @NotEmpty List<UUID> authorIds,
        @NotBlank @Size(max = 100) String sku,
        @NotBlank @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") @Size(max = 240) String slug,
        @NotNull @Valid MoneyDto regularPrice,
        @Valid MoneyDto promotionalPrice,
        String commercialDescription,
        @NotNull BookStatus status,
        boolean featured,
        boolean newArrival,
        @NotEmpty List<@Valid CategoryAssignmentRequest> categories,
        @Valid SeoRequest seo,
        @Min(0) int initialOnHand,
        @Min(0) int minimumStock
    ) {}

    public record AuthorDto(UUID id, String name, String slug, String biography, boolean active) {}
    public record PublisherDto(UUID id, UUID organizationId, String name, String slug, String description, boolean active) {}
    public record CategorySummary(UUID id, String code, String displayName, RootType rootType, boolean primary, String href) {}
    public record InventoryDto(UUID id, int onHand, int reserved, int available, int minimumStock, String status, long version) {}
    public record ImageDto(UUID id, String type, String url, String altText, Integer width, Integer height, int sortOrder, boolean authorized) {}
    public record SeoDto(String seoTitle, String metaDescription, String canonicalUrl, RobotsPolicy robotsPolicy, String ogTitle, String ogDescription, String ogImageUrl) {}

    public record BookResponse(
        UUID id,
        String isbn13,
        String isbn10,
        String title,
        String subtitle,
        String edition,
        Integer publicationYear,
        Integer pages,
        String language,
        String bindingFormat,
        String bibliographicDescription,
        PublisherDto publisher,
        List<AuthorDto> authors,
        String sku,
        String slug,
        String canonicalPath,
        MoneyDto regularPrice,
        MoneyDto promotionalPrice,
        String commercialDescription,
        BookStatus status,
        boolean featured,
        boolean newArrival,
        List<CategorySummary> categories,
        InventoryDto inventory,
        List<ImageDto> images,
        SeoDto seo
    ) {}

    public record CategoryRequest(
        UUID parentId,
        @NotNull RootType rootType,
        @NotBlank @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") @Size(max = 120) String code,
        @NotBlank @Size(max = 220) String displayName,
        @Size(max = 220) String slugCandidate,
        @Size(max = 500) String publicPath,
        String description,
        @Min(0) int sortOrder,
        boolean active,
        boolean visible,
        boolean showInMenu,
        @Valid SeoRequest seo
    ) {}

    public record CategoryResponse(
        UUID id,
        UUID parentId,
        RootType rootType,
        String code,
        String displayName,
        String slugCandidate,
        String publicPath,
        String href,
        String description,
        int sortOrder,
        boolean active,
        boolean visible,
        boolean showInMenu,
        SeoDto seo
    ) {}

    public record NavigationNode(UUID id, String code, String label, String href, RootType rootType, int sortOrder, List<NavigationNode> children) {}

    public record AuthorRequest(@NotBlank @Size(max = 220) String name,
                                @NotBlank @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") String slug,
                                String biography, boolean active) {}

    public record PublisherRequest(@NotBlank @Size(max = 200) String legalName,
                                   @Size(max = 200) String tradeName,
                                   @Size(max = 40) String taxId,
                                   @NotBlank @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") String slug,
                                   String description,
                                   boolean active) {}

    public record InventoryAdjustmentRequest(int delta, @Min(0) int minimumStock, @NotBlank @Size(max = 300) String reason) {}

    public record LoginHint(@Email String email) {}
}

