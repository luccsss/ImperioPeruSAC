package pe.com.imperioperu.catalog.audit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "audit_event")
public class AuditEvent {
    @Id private UUID id;
    private String actor;
    private String action;
    @Column(name = "entity_type") private String entityType;
    @Column(name = "entity_id") private String entityId;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "before_data", columnDefinition = "jsonb") private String beforeData;
    @JdbcTypeCode(SqlTypes.JSON) @Column(name = "after_data", columnDefinition = "jsonb") private String afterData;
    @Column(name = "request_id") private String requestId;
    @Column(name = "occurred_at") private Instant occurredAt;

    protected AuditEvent() {}

    public AuditEvent(String actor, String action, String entityType, String entityId, String beforeData, String afterData) {
        this.id = UUID.randomUUID();
        this.actor = actor;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.beforeData = beforeData;
        this.afterData = afterData;
        this.occurredAt = Instant.now();
    }
}

