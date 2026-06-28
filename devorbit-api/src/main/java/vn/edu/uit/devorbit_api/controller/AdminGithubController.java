package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.GithubScanRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.service.GithubScanService;
import vn.edu.uit.devorbit_api.service.GithubAutoApprovalService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ADMIN GITHUB CONTROLLER = scan GitHub for repos matching course topics.
 *
 * Uses the GitHub API (via GithubScanService) to search for repos
 * that match course keywords. Found repos become "candidates" that
 * can be approved or rejected by admins.
 *
 * All endpoints require ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/github")
@RequiredArgsConstructor
public class AdminGithubController {

    private final GithubScanService githubScanService;
    private final GithubAutoApprovalService githubAutoApprovalService;
    private final ExecutorService scanExecutor = Executors.newSingleThreadExecutor(
            r -> Thread.ofVirtual().name("github-scan").unstarted(r));

    /**
     * Scan GitHub for repos matching the given query parameters.
     * Returns the newly discovered repo candidates.
     */
    @PostMapping("/scan")
    public List<RepoCandidateResponse> scan(@RequestBody @Valid GithubScanRequest request) {
        return githubScanService.scan(request);
    }

    /**
     * Scan ALL courses that have scan queries configured.
     * Runs in the background (returns immediately with HTTP 202).
     * Prevents duplicate scans — returns 409 Conflict if a scan is already running.
     */
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

    /** Get the scan logs (recent activity) */
    @GetMapping("/scan-logs")
    public List<String> getScanLogs() {
        return githubScanService.getScanLogs();
    }

    /** Clear the scan logs */
    @DeleteMapping("/scan-logs")
    public ResponseEntity<Void> clearScanLogs() {
        githubScanService.clearLogs();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/auto-approve")
    public GithubAutoApprovalService.AutoApprovalRun autoApprove() {
        return githubAutoApprovalService.reviewPendingCandidates();
    }

    @GetMapping("/auto-approvals")
    public List<RepoCandidateResponse> getAutoApprovals() {
        return githubAutoApprovalService.getAutoApprovedCandidates();
    }

    @GetMapping("/automation-status")
    public Map<String, Object> getAutomationStatus() {
        return Map.of(
            "enabled", true,
            "cron", "0 0 3 * * SAT",
            "zone", "UTC",
            "description", "Every Saturday at 03:00 UTC"
        );
    }
}
