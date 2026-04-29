package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.GithubScanRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.service.GithubScanService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/github")
@RequiredArgsConstructor
public class AdminGithubController {

    private final GithubScanService githubScanService;

    @PostMapping("/scan")
    public List<RepoCandidateResponse> scan(@RequestBody @Valid GithubScanRequest request) {
        return githubScanService.scan(request);
    }
}
