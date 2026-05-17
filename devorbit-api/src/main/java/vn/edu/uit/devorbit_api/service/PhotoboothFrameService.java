package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
