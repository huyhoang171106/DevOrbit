package vn.edu.uit.devorbit_api.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

/**
 * PHOTOBOOTH FRAME = a template for the AI photobooth feature.
 *
 * Maps to the "photobooth_frames" table.
 * Each frame defines how a photobooth photo looks:
 * - Position of photo slots (where images go)
 * - Overlay image (decorative elements)
 * - Filter and background color
 * - How many photos the frame holds
 *
 * Used by the AI Photobooth feature where students take themed photos.
 */
@Entity
@Table(name = "photobooth_frames")
public class PhotoboothFrame {

    /** UUID primary key (auto-generated if null) */
    @Id
    private UUID id;

    /** Human-readable unique identifier (e.g., "graduation-2024") */
    @Column(name = "frame_id", nullable = false, unique = true)
    private String frameId;

    /** Internal name */
    @Column(nullable = false)
    private String name;

    /** Display name shown to users */
    @Column(name = "display_name", nullable = false)
    private String displayName;

    /** How many photos this frame can hold */
    @Column(name = "photo_count", nullable = false)
    private Integer photoCount;

    /** Description of the frame */
    @Column(columnDefinition = "text default ''")
    private String description;

    /** JSON array defining photo slot positions */
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode slots;

    /** URL to the overlay image (PNG with transparency) */
    @Column(name = "overlay_image_url", columnDefinition = "TEXT")
    private String overlayImageUrl;

    /** CSS filter to apply */
    @Column(columnDefinition = "text default 'normal'")
    private String filter;

    /** Background color (hex, default #ffffff) */
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

    // =====================================================================
    // GETTERS & SETTERS
    // =====================================================================
    // Note: This entity uses manual getters/setters instead of Lombok
    // because it has custom @PrePersist/@PreUpdate lifecycle methods.
    // =====================================================================

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
