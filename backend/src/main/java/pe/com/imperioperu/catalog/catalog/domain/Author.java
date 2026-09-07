package pe.com.imperioperu.catalog.catalog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "author")
public class Author {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    private String name;
    private String slug;
    private String biography;
    private boolean active = true;
    @Column(name = "created_at") private Instant createdAt;
    @Column(name = "updated_at") private Instant updatedAt;

    protected Author() {}
    public Author(String name, String slug, String biography) {
        this.name = name;
        this.slug = slug;
        this.biography = biography;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public String getBiography() { return biography; }
    public boolean isActive() { return active; }
    public void update(String name, String slug, String biography, boolean active) {
        this.name = name;
        this.slug = slug;
        this.biography = biography;
        this.active = active;
        this.updatedAt = Instant.now();
    }
}

