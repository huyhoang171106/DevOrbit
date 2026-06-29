package vn.edu.uit.devorbit_api.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import vn.edu.uit.devorbit_api.dto.admin.CandidateReviewRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.repository.RepoCandidateRepository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class GithubAutoApprovalService {
    public static final String REVIEW_NOTE_PREFIX = "AUTO_APPROVED_UIT: ";
    private static final Pattern UIT_TOKEN = Pattern.compile("(^|[^a-z0-9])uit([^a-z0-9]|$)", Pattern.CASE_INSENSITIVE);
    private static final Logger log = LoggerFactory.getLogger(GithubAutoApprovalService.class);

    private final RepoCandidateRepository repoCandidateRepository;
    private final RepoCandidateService repoCandidateService;
    private final GithubScanService githubScanService;
    private final WebClient githubWebClient;
    private final ObjectMapper objectMapper;

    public record AutoApprovalRun(int checked, int approved, int leftForManualReview) {}

    public AutoApprovalRun reviewPendingCandidates() {
        List<RepoCandidate> pending = repoCandidateRepository.findByStatus(RepoCandidateStatus.NEW);
        Map<String, String> ownerEvidence = new ConcurrentHashMap<>();
        int approved = 0;

        for (RepoCandidate candidate : pending) {
            try {
                String evidence = findUitEvidence(candidate, ownerEvidence);
                if (evidence == null) continue;

                repoCandidateService.approveCandidate(
                    candidate.getId(),
                    new CandidateReviewRequest(null, null, REVIEW_NOTE_PREFIX + evidence)
                );
                approved++;
            } catch (Exception exception) {
                log.warn("Auto approval failed for candidate {}: {}", candidate.getId(), exception.getMessage());
            }
        }

        return new AutoApprovalRun(pending.size(), approved, pending.size() - approved);
    }

    public List<RepoCandidateResponse> getAutoApprovedCandidates() {
        return repoCandidateRepository
            .findByStatusAndReviewNoteStartingWithOrderByApprovedAtDesc(
                RepoCandidateStatus.APPROVED,
                REVIEW_NOTE_PREFIX
            )
            .stream()
            .map(RepoCandidateResponse::from)
            .toList();
    }

    String findUitEvidence(RepoCandidate candidate, Map<String, String> ownerEvidence) {
        GithubScanService.RepositoryContext context = githubScanService.fetchRepositoryContext(
            candidate.getGithubOwner(), candidate.getGithubName());
        candidate.setReadmeExcerpt(context.readmeExcerpt());
        candidate.setFileTree(context.fileTree());
        candidate.setHasReadme(context.hasReadme());

        if (containsUit(candidate.getGithubName(), candidate.getDescription(), candidate.getTopics(),
            context.readmeExcerpt(), context.fileTree())) {
            repoCandidateRepository.save(candidate);
            return "repo contains UIT";
        }

        String owner = candidate.getGithubOwner();
        if (owner == null || owner.isBlank()) return null;
        String cached = ownerEvidence.computeIfAbsent(owner.toLowerCase(Locale.ROOT), ignored -> inspectOwner(owner));
        return cached.isBlank() ? null : cached;
    }

    @SneakyThrows
    private String inspectOwner(String owner) {
        try {
            String profileJson = githubWebClient.get()
                .uri("/users/{owner}", owner)
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(8));
            if (profileJson != null) {
                JsonNode profile = objectMapper.readTree(profileJson);
                if (containsUit(profile.path("bio").asText(null))) {
                    return "GitHub bio contains UIT";
                }
            }

            String reposJson = githubWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/users/{owner}/repos")
                    .queryParam("per_page", 100)
                    .queryParam("sort", "updated")
                    .build(owner))
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(12));
            if (reposJson != null) {
                JsonNode repos = objectMapper.readTree(reposJson);
                if (repos.isArray()) {
                    for (JsonNode repo : repos) {
                        List<String> values = new ArrayList<>();
                        values.add(repo.path("name").asText(null));
                        values.add(repo.path("description").asText(null));
                        repo.path("topics").forEach(topic -> values.add(topic.asText(null)));
                        if (containsUit(values.toArray(String[]::new))) {
                            return "another owner repo contains UIT: " + repo.path("name").asText("unknown");
                        }
                    }
                }
            }
        } catch (Exception exception) {
            log.warn("Could not inspect GitHub owner {} for UIT evidence: {}", owner, exception.getMessage());
        }
        return "";
    }

    static boolean containsUit(String... values) {
        if (values == null) return false;
        for (String value : values) {
            if (value != null && UIT_TOKEN.matcher(value).find()) return true;
        }
        return false;
    }
}
