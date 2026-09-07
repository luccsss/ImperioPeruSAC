package pe.com.imperioperu.catalog.importer.domain;

public enum ImportDisposition {
    INVALID,
    DUPLICATE_IN_FILE,
    NEW_REQUIRES_COMMERCIAL_DATA,
    MATCHED_NO_CHANGES,
    NEEDS_REVIEW,
    APPROVED,
    REJECTED,
    APPLIED
}

