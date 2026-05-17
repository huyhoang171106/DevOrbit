package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.GithubScanRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.service.GithubScanService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/admin/github")
@RequiredArgsConstructor
public class AdminGithubController {

    private final GithubScanService githubScanService;
    private final ExecutorService scanExecutor = Executors.newSingleThreadExecutor(
            r -> Thread.ofVirtual().name("github-scan").unstarted(r));

    @PostMapping("/scan")
    public List<RepoCandidateResponse> scan(@RequestBody @Valid GithubScanRequest request) {
        return githubScanService.scan(request);
    }

    @PostMapping("/scan-all")
    public ResponseEntity<Map<String, String>> scanAll() {
        if (githubScanService.isScanRunning()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "A bulk scan is already in progress"));
        }
        scanExecutor.submit(() -> githubScanService.scanAll());
        return ResponseEntity.accepted()
                .body(Map.of("message", "Bulk scan started in background"));
    }

    @GetMapping("/scan-logs")
    public List<String> getScanLogs() {
        return githubScanService.getScanLogs();
    }
}
