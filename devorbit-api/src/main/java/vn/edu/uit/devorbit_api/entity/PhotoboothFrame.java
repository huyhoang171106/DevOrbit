package vn.edu.uit.devorbit_api.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "photobooth_frames")
public class PhotoboothFrame {

    @Id
    private UUID id;

    @Column(name = "frame_id", nullable = false, unique = true)
    private String frameId;

    @Column(nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "photo_count", nullable = false)
    private Integer photoCount;

    @Column(columnDefinition = "text default ''")
    private String description;

    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode slots;

    @Column(name = "overlay_image_url")
    private String overlayImageUrl;

    @Column(columnDefinition = "text default 'normal'")
    private String filter;

    @Column(name = "background_color", columnDefinition = "text default '#ffffff'")
    private String backgroundColor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFrameId() { return frameId; }
    public void setFrameId(String frameId) { this.frameId = frameId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Integer getPhotoCount() { return photoCount; }
    public void setPhotoCount(Integer photoCount) { this.photoCount = photoCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public JsonNode getSlots() { return slots; }
    public void setSlots(JsonNode slots) { this.slots = slots; }

    public String getOverlayImageUrl() { return overlayImageUrl; }
    public void setOverlayImageUrl(String overlayImageUrl) { this.overlayImageUrl = overlayImageUrl; }

    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }

    public String getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
