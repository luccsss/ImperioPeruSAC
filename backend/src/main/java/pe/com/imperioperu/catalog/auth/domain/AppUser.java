package pe.com.imperioperu.catalog.auth.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "app_user")
public class AppUser {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    private String email;
    @Column(name = "password_hash") private String passwordHash;
    @Column(name = "display_name") private String displayName;
    private boolean enabled = true;
    @Column(name = "token_version") private int tokenVersion;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "updated_at") private Instant updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role") @Enumerated(EnumType.STRING)
    private Set<AdminRole> roles = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permission", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "permission") @Enumerated(EnumType.STRING)
    private Set<Permission> permissions = new LinkedHashSet<>();

    protected AppUser() {}
    public AppUser(String email, String passwordHash, String displayName, Set<AdminRole> roles, Set<Permission> permissions) {
        this.email = email.toLowerCase().trim();
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.roles.addAll(roles);
        this.permissions.addAll(permissions);
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public boolean isEnabled() { return enabled; }
    public int getTokenVersion() { return tokenVersion; }
    public Set<AdminRole> getRoles() { return Set.copyOf(roles); }
    public Set<Permission> getPermissions() { return Set.copyOf(permissions); }
}

