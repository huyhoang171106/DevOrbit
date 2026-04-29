package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.service.TechStackService;

import java.util.List;

@RestController
@RequestMapping("/api/repos/{repoId}/tech-stacks")
@RequiredArgsConstructor
public class PublicTechStackController {
    private final TechStackService techStackService;

    @GetMapping
    public List<TechStackResponse> getTechStacks(@PathVariable Long repoId) {
        return techStackService.getTechStacksByRepo(repoId);
    }
}
