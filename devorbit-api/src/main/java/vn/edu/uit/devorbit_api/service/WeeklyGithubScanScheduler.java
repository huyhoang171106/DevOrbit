package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WeeklyGithubScanScheduler {
    private static final Logger log = LoggerFactory.getLogger(WeeklyGithubScanScheduler.class);

    private final GithubScanService githubScanService;
    private final GithubAutoApprovalService autoApprovalService;

    @Scheduled(cron = "0 0 3 * * SAT", zone = "UTC")
    public void scanAndAutoApprove() {
        log.info("Starting scheduled Saturday 03:00 UTC GitHub scan");
        githubScanService.scanAll();
        GithubAutoApprovalService.AutoApprovalRun result = autoApprovalService.reviewPendingCandidates();
        log.info("Scheduled GitHub auto review completed: checked={}, approved={}, manual={}",
            result.checked(), result.approved(), result.leftForManualReview());
    }
}
