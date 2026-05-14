package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.PhotoboothFrameDTO;
import vn.edu.uit.devorbit_api.entity.PhotoboothFrame;
import vn.edu.uit.devorbit_api.repository.PhotoboothFrameRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoboothFrameService {

    private final PhotoboothFrameRepository repository;
    private final ObjectMapper objectMapper;

    public List<PhotoboothFrameDTO> listFrames() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public PhotoboothFrameDTO getFrame(String frameId) {
        return repository.findByFrameId(frameId)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public PhotoboothFrameDTO upsertFrame(PhotoboothFrameDTO dto) {
        PhotoboothFrame entity = repository.findByFrameId(dto.getFrameId())
                .orElseGet(PhotoboothFrame::new);

        entity.setFrameId(dto.getFrameId());
        entity.setName(dto.getName());
        entity.setDisplayName(dto.getDisplayName());
        entity.setPhotoCount(dto.getPhotoCount());
        entity.setDescription(dto.getDescription() != null ? dto.getDescription() : "");
        entity.setSlots(parseSlots(dto.getSlots()));
        entity.setOverlayImageUrl(dto.getOverlayImageUrl());
        entity.setFilter(dto.getFilter() != null ? dto.getFilter() : "normal");
        entity.setBackgroundColor(dto.getBackgroundColor() != null ? dto.getBackgroundColor() : "#ffffff");

        entity = repository.save(entity);
        return toDTO(entity);
    }

    @Transactional
    public boolean deleteFrame(String frameId) {
        if (repository.findByFrameId(frameId).isEmpty()) return false;
        repository.deleteByFrameId(frameId);
        return true;
    }

    @Transactional
    public void updateOverlayImage(String frameId, String imageUrl) {
        repository.findByFrameId(frameId).ifPresent(frame -> {
            frame.setOverlayImageUrl(imageUrl);
            repository.save(frame);
        });
    }

    // Seed default frames on first startup
    @PostConstruct
    @Transactional
    public void seedDefaults() {
        if (repository.count() > 0) return;

        upsertFrame(new PhotoboothFrameDTO(
                "classic-4", "Classic", "Classic 4-Hình", 4,
                "Phong cách photobooth cổ điển 4 tấm",
                "[" +
                        "{\"id\":\"s1\",\"x\":120,\"y\":120,\"width\":1760,\"height\":380,\"borderRadius\":24}," +
                        "{\"id\":\"s2\",\"x\":120,\"y\":540,\"width\":1760,\"height\":380,\"borderRadius\":24}," +
                        "{\"id\":\"s3\",\"x\":120,\"y\":960,\"width\":1760,\"height\":380,\"borderRadius\":24}," +
                        "{\"id\":\"s4\",\"x\":120,\"y\":1380,\"width\":1760,\"height\":380,\"borderRadius\":24}" +
                        "]",
                null, "normal", "#1a1a2e"
        ));

        upsertFrame(new PhotoboothFrameDTO(
                "duo-2", "Duo", "Song Hỷ", 2,
                "Hai tấm ảnh song song",
                "[" +
                        "{\"id\":\"s1\",\"x\":100,\"y\":200,\"width\":850,\"height\":1100,\"borderRadius\":32}," +
                        "{\"id\":\"s2\",\"x\":1050,\"y\":200,\"width\":850,\"height\":1100,\"borderRadius\":32}" +
                        "]",
                null, "normal", "#0f0f1a"
        ));

        upsertFrame(new PhotoboothFrameDTO(
                "single-1", "Single", "Cá Nhân", 1,
                "Một tấm ảnh lớn",
                "[" +
                        "{\"id\":\"s1\",\"x\":150,\"y\":150,\"width\":1700,\"height\":1200,\"borderRadius\":40}" +
                        "]",
                null, "normal", "#09090b"
        ));
    }

    private JsonNode parseSlots(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return objectMapper.createArrayNode();
        }
    }

    private PhotoboothFrameDTO toDTO(PhotoboothFrame entity) {
        String slotsJson;
        try {
            JsonNode slotsNode = entity.getSlots();
            if (slotsNode == null) {
                slotsJson = "[]";
            } else if (slotsNode.isTextual()) {
                // Legacy data: stored as JSON string (e.g. "" or "[...]")
                // Re-parse the text content to get proper JSON array
                String text = slotsNode.asText();
                if (text == null || text.isEmpty()) {
                    slotsJson = "[]";
                } else {
                    JsonNode reparsed = objectMapper.readTree(text);
                    slotsJson = objectMapper.writeValueAsString(reparsed);
                }
            } else {
                slotsJson = objectMapper.writeValueAsString(slotsNode);
            }
        } catch (Exception e) {
            slotsJson = "[]";
        }

        return new PhotoboothFrameDTO(
                entity.getFrameId(),
                entity.getName(),
                entity.getDisplayName(),
                entity.getPhotoCount(),
                entity.getDescription(),
                slotsJson,
                entity.getOverlayImageUrl(),
                entity.getFilter(),
                entity.getBackgroundColor()
        );
    }
}
