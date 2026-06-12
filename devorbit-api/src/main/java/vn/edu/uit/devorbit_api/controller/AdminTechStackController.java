package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.AdminTechStackResponse;
import vn.edu.uit.devorbit_api.entity.TechStack;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/techstack")
@RequiredArgsConstructor
public class AdminTechStackController {

    private final TechStackRepository techStackRepo;

    @GetMapping
    public ResponseEntity<List<AdminTechStackResponse>> list() {
        return ResponseEntity.ok(techStackRepo.findAllDistinctOrderByName()
            .stream().map(this::toResponse)
            .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<AdminTechStackResponse> create(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (techStackRepo.findByNameIgnoreCase(name.trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        TechStack techStack = TechStack.builder().name(name.trim()).build();
        return ResponseEntity.ok(toResponse(techStackRepo.save(techStack)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!techStackRepo.existsById(id)) {
            throw new NotFoundException("Tech stack not found");
        }
        techStackRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private AdminTechStackResponse toResponse(TechStack ts) {
        return AdminTechStackResponse.builder()
            .id(ts.getId())
            .name(ts.getName())
            .build();
    }
}
