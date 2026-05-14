package vn.edu.uit.devorbit_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.uit.devorbit_api.service.CloudinaryService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "Các API liên quan đến upload file")
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    @Operation(summary = "Upload ảnh lên Cloudinary")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<?, ?>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<?, ?> data = cloudinaryService.upload(file);
        return ResponseEntity.ok(data);
    }
}
