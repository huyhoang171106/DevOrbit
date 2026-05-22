package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.service.TechStackService;

import java.util.List;

/**
 * PUBLIC TECH STACK CONTROLLER = browse available tech stacks.
 *
 * No authentication required.
 */
@RestController
@RequiredArgsConstructor
public class PublicTechStackController {
    private final TechStackService techStackService;

    /** Get ALL tech stacks (list of technology names) */
    @GetMapping("/api/tech-stacks")
    public List<TechStackResponse> getAllTechStacks() {
        return techStackService.getAllTechStacks();
    }
}
