package vn.edu.uit.devorbit_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.devorbit_api.dto.PhotoboothFrameDTO;
import vn.edu.uit.devorbit_api.service.CloudinaryService;
import vn.edu.uit.devorbit_api.service.PhotoboothFrameService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/photobooth/frames")
@RequiredArgsConstructor
@Tag(name = "Photobooth", description = "API quản lý frame Photobooth")
public class PhotoboothFrameController {

    private final PhotoboothFrameService frameService;
    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Lấy danh sách tất cả frame")
    @GetMapping
    public ResponseEntity<List<PhotoboothFrameDTO>> listFrames() {
        return ResponseEntity.ok(frameService.listFrames());
    }

    @Operation(summary = "Lấy frame theo frameId")
    @GetMapping("/{frameId}")
    public ResponseEntity<PhotoboothFrameDTO> getFrame(@PathVariable String frameId) {
        PhotoboothFrameDTO dto = frameService.getFrame(frameId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Tạo hoặc cập nhật frame")
    @PostMapping
    public ResponseEntity<PhotoboothFrameDTO> upsertFrame(@RequestBody PhotoboothFrameDTO dto) {
        return ResponseEntity.ok(frameService.upsertFrame(dto));
    }

    @Operation(summary = "Xoá frame")
    @DeleteMapping("/{frameId}")
    public ResponseEntity<Void> deleteFrame(@PathVariable String frameId) {
        return frameService.deleteFrame(frameId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Upload ảnh overlay cho frame")
    @PostMapping(value = "/{frameId}/overlay", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadOverlay(
            @PathVariable String frameId,
            @RequestParam("file") MultipartFile file) {
        Map<?, ?> uploadData = cloudinaryService.upload(file);
        String url = (String) uploadData.get("url");
        if (url == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Upload failed"));
        }
        frameService.updateOverlayImage(frameId, url);
        return ResponseEntity.ok(Map.of("url", url));
    }
}
