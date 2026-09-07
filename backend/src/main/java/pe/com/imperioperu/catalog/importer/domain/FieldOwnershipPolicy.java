package pe.com.imperioperu.catalog.importer.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "field_ownership_policy")
public class FieldOwnershipPolicy {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "catalog_source_id") private CatalogSource catalogSource;
    private String fieldName;
    @Enumerated(EnumType.STRING) private FieldOwnership ownership;
    @Enumerated(EnumType.STRING) private SyncPolicy syncPolicy;
    private boolean active;
    protected FieldOwnershipPolicy() {}
    public String getFieldName() { return fieldName; }
    public FieldOwnership getOwnership() { return ownership; }
    public SyncPolicy getSyncPolicy() { return syncPolicy; }
}

