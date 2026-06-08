package vn.edu.uit.devorbit_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import vn.edu.uit.devorbit_api.exception.BadRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class SupabaseStorageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
        "image/png", "image/jpeg", "image/webp", "image/gif"
    );
    private static final byte[][] VALID_MAGIC_BYTES = {
        {(byte) 0x89, 'P', 'N', 'G', (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A}, // PNG
        {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}, // JPEG
        {'R', 'I', 'F', 'F'}, // WEBP
        {'G', 'I', 'F', '8'}  // GIF
    };

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private final WebClient webClient;

    public SupabaseStorageService() {
        this.webClient = WebClient.create();
    }

    public Map<String, String> upload(MultipartFile file) {
        validateFile(file);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String uploadUrl = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucketName, fileName);

        try {
            byte[] fileBytes = file.getBytes();
            
            webClient.post()
                    .uri(uploadUrl)
                    .header("Authorization", "Bearer " + supabaseKey)
                    .header("apikey", supabaseKey)
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .bodyValue(fileBytes)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucketName, fileName);
            return Map.of("url", publicUrl);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Supabase Storage", e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File too large. Maximum size is 5MB.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only image files allowed (PNG, JPEG, WebP, GIF).");
        }

        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[8];
            int bytesRead = is.read(header);
            if (bytesRead < 3) {
                throw new BadRequestException("Invalid or empty file.");
            }
            boolean valid = false;
            for (byte[] magic : VALID_MAGIC_BYTES) {
                if (startsWith(header, magic)) {
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                throw new BadRequestException("File content does not match an allowed image format.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file for validation", e);
        }
    }

    private boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) return false;
        }
        return true;
    }
}
