package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.*;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningRoadmapService {
    private final LearningRoadmapRepository roadmapRepo;
    private final RoadmapPhaseRepository phaseRepo;
    private final RoadmapItemRepository itemRepo;
    private final StudentUserRepository studentRepo;
    private final CourseRepository courseRepo;

    public List<RoadmapResponse> getAll() {
        return roadmapRepo.findAllByOrderByUpdatedAtDesc().stream()
                .map(this::toRoadmapResponse)
                .toList();
    }

    public RoadmapResponse getById(Long id) {
        return toRoadmapResponse(roadmapRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Roadmap not found: " + id)));
    }

    @Transactional
    public RoadmapResponse create(RoadmapRequest request) {
        StudentUser student = studentRepo.findById(request.studentId())
                .orElseThrow(() -> new NotFoundException("Student not found: " + request.studentId()));
        LearningRoadmap entity = LearningRoadmap.builder()
                .student(student)
                .title(request.title())
                .description(request.description())
                .markdownContent(request.markdownContent())
                .isPublic(request.isPublic() != null && request.isPublic())
                .build();
        return toRoadmapResponse(roadmapRepo.save(entity));
    }

    @Transactional
    public RoadmapResponse update(Long id, RoadmapRequest request) {
        LearningRoadmap entity = roadmapRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Roadmap not found: " + id));
        if (request.studentId() != null) {
            StudentUser student = studentRepo.findById(request.studentId())
                    .orElseThrow(() -> new NotFoundException("Student not found: " + request.studentId()));
            entity.setStudent(student);
        }
        if (request.title() != null) entity.setTitle(request.title());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.markdownContent() != null) entity.setMarkdownContent(request.markdownContent());
        if (request.isPublic() != null) entity.setPublic(request.isPublic());
        entity.setUpdatedAt(LocalDateTime.now());
        return toRoadmapResponse(roadmapRepo.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        LearningRoadmap entity = roadmapRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Roadmap not found: " + id));
        roadmapRepo.delete(entity);
    }

    public List<PhaseResponse> getPhases(Long roadmapId) {
        return phaseRepo.findByRoadmapIdOrderBySortOrderAsc(roadmapId).stream()
                .map(this::toPhaseResponse)
                .toList();
    }

    @Transactional
    public PhaseResponse createPhase(Long roadmapId, PhaseRequest request) {
        LearningRoadmap roadmap = roadmapRepo.findById(roadmapId)
                .orElseThrow(() -> new NotFoundException("Roadmap not found: " + roadmapId));
        RoadmapPhase entity = RoadmapPhase.builder()
                .roadmap(roadmap)
                .title(request.title())
                .description(request.description())
                .sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
                .build();
        return toPhaseResponse(phaseRepo.save(entity));
    }

    @Transactional
    public PhaseResponse updatePhase(Long phaseId, PhaseRequest request) {
        RoadmapPhase entity = phaseRepo.findById(phaseId)
                .orElseThrow(() -> new NotFoundException("Phase not found: " + phaseId));
        if (request.title() != null) entity.setTitle(request.title());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.sortOrder() != null) entity.setSortOrder(request.sortOrder());
        return toPhaseResponse(phaseRepo.save(entity));
    }

    @Transactional
    public void deletePhase(Long phaseId) {
        RoadmapPhase entity = phaseRepo.findById(phaseId)
                .orElseThrow(() -> new NotFoundException("Phase not found: " + phaseId));
        phaseRepo.delete(entity);
    }

    public List<ItemResponse> getItems(Long phaseId) {
        return itemRepo.findByPhaseIdOrderBySortOrderAsc(phaseId).stream()
                .map(this::toItemResponse)
                .toList();
    }

    @Transactional
    public ItemResponse createItem(Long phaseId, ItemRequest request) {
        RoadmapPhase phase = phaseRepo.findById(phaseId)
                .orElseThrow(() -> new NotFoundException("Phase not found: " + phaseId));
        RoadmapItem entity = RoadmapItem.builder()
                .phase(phase)
                .targetType(request.targetType())
                .targetId(request.targetId())
                .title(request.title())
                .note(request.note())
                .sortOrder(request.sortOrder() != null ? request.sortOrder() : 0)
                .build();
        return toItemResponse(itemRepo.save(entity));
    }

    @Transactional
    public ItemResponse updateItem(Long itemId, ItemRequest request) {
        RoadmapItem entity = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (request.targetType() != null) entity.setTargetType(request.targetType());
        if (request.targetId() != null) entity.setTargetId(request.targetId());
        if (request.title() != null) entity.setTitle(request.title());
        if (request.note() != null) entity.setNote(request.note());
        if (request.sortOrder() != null) entity.setSortOrder(request.sortOrder());
        return toItemResponse(itemRepo.save(entity));
    }

    @Transactional
    public void deleteItem(Long itemId) {
        RoadmapItem entity = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        itemRepo.delete(entity);
    }

    private RoadmapResponse toRoadmapResponse(LearningRoadmap entity) {
        return new RoadmapResponse(
                entity.getId(), entity.getStudent().getId(),
                entity.getStudent().getStudentCode(), entity.getStudent().getFullName(),
                entity.getTitle(), entity.getDescription(),
                entity.getMarkdownContent(), entity.isPublic(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    private PhaseResponse toPhaseResponse(RoadmapPhase entity) {
        return new PhaseResponse(
                entity.getId(), entity.getRoadmap().getId(),
                entity.getTitle(), entity.getDescription(),
                entity.getSortOrder(), entity.getCreatedAt());
    }

    private ItemResponse toItemResponse(RoadmapItem entity) {
        return new ItemResponse(
                entity.getId(), entity.getPhase().getId(),
                entity.getTargetType(), entity.getTargetId(),
                entity.getTitle(), entity.getNote(),
                entity.getSortOrder(), entity.getCreatedAt());
    }
}
