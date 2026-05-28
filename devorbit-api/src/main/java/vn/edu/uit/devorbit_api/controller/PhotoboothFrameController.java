package vn.edu.uit.devorbit_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.devorbit_api.dto.PhotoboothFrameDTO;
import vn.edu.uit.devorbit_api.service.PhotoboothFrameService;
import vn.edu.uit.devorbit_api.service.SupabaseStorageService;

import java.util.List;
import java.util.Map;

/**
 * PHOTOBOOTH FRAME CONTROLLER = manage AI Photobooth frame templates.
 *
 * Photobooth frames define how AI-generated photos are laid out:
 * - Where photos appear
 * - Background colors, filters
 * - Overlay decorations
 *
 * Public endpoints (GET) are open; write endpoints require ROLE_ADMIN.
 * See SecurityConfig for the rule:
 *   GET /api/photobooth/** → permitAll
 *   all other /api/photobooth/** → ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/photobooth/frames")
@RequiredArgsConstructor
@Tag(name = "Photobooth", description = "Photobooth frame management")
public class PhotoboothFrameController {

    private final PhotoboothFrameService frameService;
    private final SupabaseStorageService storageService;

    /** Get ALL photobooth frames */
    @Operation(summary = "List all frames")
    @GetMapping
    public ResponseEntity<List<PhotoboothFrameDTO>> listFrames() {
        return ResponseEntity.ok(frameService.listFrames());
    }

    /** Get ONE frame by its frameId (not the database ID) */
    @Operation(summary = "Get frame by frameId")
    @GetMapping("/{frameId}")
    public ResponseEntity<PhotoboothFrameDTO> getFrame(@PathVariable String frameId) {
        PhotoboothFrameDTO dto = frameService.getFrame(frameId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    /** Create a NEW frame or UPDATE an existing one */
    @Operation(summary = "Create or update a frame")
    @PostMapping
    public ResponseEntity<PhotoboothFrameDTO> upsertFrame(@RequestBody PhotoboothFrameDTO dto) {
        return ResponseEntity.ok(frameService.upsertFrame(dto));
    }

    /** Delete a frame */
    @Operation(summary = "Delete a frame")
    @DeleteMapping("/{frameId}")
    public ResponseEntity<Void> deleteFrame(@PathVariable String frameId) {
        return frameService.deleteFrame(frameId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    /** Upload an overlay image (PNG with transparency) for a frame */
    @Operation(summary = "Upload overlay image for a frame")
    @PostMapping(value = "/{frameId}/overlay", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadOverlay(
            @PathVariable String frameId,
            @RequestParam("file") MultipartFile file) {
        Map<?, ?> uploadData = storageService.upload(file);
        String url = (String) uploadData.get("url");
        if (url == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Upload failed"));
        }
        frameService.updateOverlayImage(frameId, url);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
