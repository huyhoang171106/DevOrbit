package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.publicapi.AiResponse;
import vn.edu.uit.devorbit_api.service.AiService;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class PublicAiController {
    private final AiService aiService;

    @GetMapping("/repo/{repoId}/summary")
    public AiResponse getRepoSummary(@PathVariable Long repoId) {
        return aiService.getRepoAiSummary(repoId);
    }

    @GetMapping("/repo/{repoId}/advice")
    public AiResponse getTutorAdvice(@PathVariable Long repoId) {
        return aiService.getTutorAdvice(repoId);
    }
}
