package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.*;
import vn.edu.uit.devorbit_api.service.LearningRoadmapService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roadmaps")
@RequiredArgsConstructor
public class AdminRoadmapController {

    private final LearningRoadmapService roadmapService;

    @GetMapping
    public List<RoadmapResponse> list() {
        return roadmapService.getAll();
    }

    @GetMapping("/{id}")
    public RoadmapResponse detail(@PathVariable Long id) {
        return roadmapService.getById(id);
    }

    @PostMapping
    public RoadmapResponse create(@RequestBody @Valid RoadmapRequest request) {
        return roadmapService.create(request);
    }

    @PutMapping("/{id}")
    public RoadmapResponse update(@PathVariable Long id, @RequestBody @Valid RoadmapRequest request) {
        return roadmapService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roadmapService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{roadmapId}/phases")
    public List<PhaseResponse> getPhases(@PathVariable Long roadmapId) {
        return roadmapService.getPhases(roadmapId);
    }

    @PostMapping("/{roadmapId}/phases")
    public PhaseResponse createPhase(
            @PathVariable Long roadmapId,
            @RequestBody @Valid PhaseRequest request) {
        return roadmapService.createPhase(roadmapId, request);
    }

    @PutMapping("/phases/{phaseId}")
    public PhaseResponse updatePhase(
            @PathVariable Long phaseId,
            @RequestBody @Valid PhaseRequest request) {
        return roadmapService.updatePhase(phaseId, request);
    }

    @DeleteMapping("/phases/{phaseId}")
    public ResponseEntity<Void> deletePhase(@PathVariable Long phaseId) {
        roadmapService.deletePhase(phaseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/phases/{phaseId}/items")
    public List<ItemResponse> getItems(@PathVariable Long phaseId) {
        return roadmapService.getItems(phaseId);
    }

    @PostMapping("/phases/{phaseId}/items")
    public ItemResponse createItem(
            @PathVariable Long phaseId,
            @RequestBody @Valid ItemRequest request) {
        return roadmapService.createItem(phaseId, request);
    }

    @PutMapping("/items/{itemId}")
    public ItemResponse updateItem(
            @PathVariable Long itemId,
            @RequestBody @Valid ItemRequest request) {
        return roadmapService.updateItem(itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        roadmapService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
