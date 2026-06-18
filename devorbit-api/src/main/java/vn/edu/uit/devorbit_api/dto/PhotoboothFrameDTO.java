package vn.edu.uit.devorbit_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * DTO for a photobooth frame configuration.
 * Used in the photobooth feature (e.g. GET /api/photobooth/frames, admin CRUD).
 * Each frame defines a visual template with slots for photos, overlay, filter, and background color.
 *
 * <p>Example usage: a graduation photobooth frame with 2 photo slots, a "sepia" filter,
 * and a school logo overlay.</p>
 */
public class PhotoboothFrameDTO {

    private static final ObjectMapper SHARED_MAPPER = new ObjectMapper();

    /** Unique identifier for this frame template. */
    private String frameId;
    /** Internal code name (e.g. "grad-2026"). */
    private String name;
    /** Human-readable name shown to users (e.g. "Graduation 2026"). */
    private String displayName;
    /** Number of photo slots in this frame. */
    private Integer photoCount;
    /** Description of the frame template. */
    private String description;
    /** Raw JSON string defining photo slot positions (e.g. "[{\"id\":\"s1\",...}]"). */
    private String slots;
    /** URL/path to the overlay image placed on top of the photos (e.g. a decorative frame border). */
    private String overlayImageUrl;
    /** CSS filter name applied to the final composite (e.g. "sepia", "grayscale", null = none). */
    private String filter;
    /** Background color behind the photos (hex code like "#FFFFFF" or named color). */
    private String backgroundColor;

    public PhotoboothFrameDTO() {}

    public PhotoboothFrameDTO(String frameId, String name, String displayName,
                              Integer photoCount, String description, String slots,
                              String overlayImageUrl, String filter, String backgroundColor) {
        this.frameId = frameId;
        this.name = name;
        this.displayName = displayName;
        this.photoCount = photoCount;
        this.description = description;
        this.slots = slots;
        this.overlayImageUrl = overlayImageUrl;
        this.filter = filter;
        this.backgroundColor = backgroundColor;
    }

    // --- Getters ---

    public String getFrameId() { return frameId; }
    public String getName() { return name; }
    public String getDisplayName() { return displayName; }
    public Integer getPhotoCount() { return photoCount; }
    public String getDescription() { return description; }

    @JsonRawValue
    public String getSlots() { return slots; }

    public String getOverlayImageUrl() { return overlayImageUrl; }
    public String getFilter() { return filter; }
    public String getBackgroundColor() { return backgroundColor; }

    // --- Setters ---

    public void setFrameId(String frameId) { this.frameId = frameId; }
    public void setName(String name) { this.name = name; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setPhotoCount(Integer photoCount) { this.photoCount = photoCount; }
    public void setDescription(String description) { this.description = description; }

    /**
     * Accepts both a JSON array (from admin POST) and a pre-serialized JSON string.
     */
    @JsonProperty("slots")
    public void setSlots(Object slots) {
        if (slots == null) {
            this.slots = "[]";
        } else if (slots instanceof String) {
            this.slots = (String) slots;
        } else {
            try {
                this.slots = SHARED_MAPPER.writeValueAsString(slots);
            } catch (Exception e) {
                this.slots = "[]";
            }
        }
    }

    public void setOverlayImageUrl(String overlayImageUrl) { this.overlayImageUrl = overlayImageUrl; }
    public void setFilter(String filter) { this.filter = filter; }
    public void setBackgroundColor(String backgroundColor) { this.backgroundColor = backgroundColor; }
}
