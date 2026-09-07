package pe.com.imperioperu.catalog.audit.application;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pe.com.imperioperu.catalog.audit.domain.AuditEvent;
import pe.com.imperioperu.catalog.audit.domain.AuditEventRepository;

@Service
public class AuditService {
    private final AuditEventRepository repository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditEventRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void record(String action, String entityType, Object entityId, Map<String, ?> before, Map<String, ?> after) {
        repository.save(new AuditEvent(actor(), action, entityType, String.valueOf(entityId), json(before), json(after)));
    }

    private String actor() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }

    private String json(Map<String, ?> value) {
        if (value == null) return null;
        try { return objectMapper.writeValueAsString(value); }
        catch (JacksonException exception) { throw new IllegalStateException("Unable to serialize audit data", exception); }
    }
}
