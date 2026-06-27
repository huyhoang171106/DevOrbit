package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.ApprovedRepoUpdateRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.TechStack;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import com.fasterxml.jackson.databind.JsonNode;
import vn.edu.uit.devorbit_api.repository.*;
import vn.edu.uit.devorbit_api.service.ai.RepoEvaluationService;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import vn.edu.uit.devorbit_api.entity.NoteTargetType;

@Service
@RequiredArgsConstructor
public class GithubRepoService {
    private static final Logger log = LoggerFactory.getLogger(GithubRepoService.class);
    private final GithubRepoRepository githubRepoRepository;
    private final TechStackRepository techStackRepository;
    private final CourseRepository courseRepository;
    private final GithubScanService githubScanService;
    private final StudentBookmarkRepository studentBookmarkRepository;
    private final NoteRepository noteRepository;
    private final RepoVoteRepository repoVoteRepository;
    private final RepoReviewRepository repoReviewRepository;
    private final RepoEvaluationService repoEvaluationService;
    private final StudentNotificationService studentNotificationService;
    private final CacheManager cacheManager;

    // Self-inject for proxy-aware @Cacheable + @Async from same class
    @Autowired @Lazy
    private GithubRepoService self;

    // =====================================================================
    // READ METHODS — cached, transactional
    // =====================================================================

    @Transactional(readOnly = true)
    @Cacheable(value = "repoById", unless = "#result == null")
    public RepoSummaryResponse getApprovedRepoById(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));
        if (!repo.isActive()) {
            throw new NotFoundException("Repo not found: " + repoId);
        }
        // Track view asynchronously
        try { self.asyncTrackView(repoId); } catch (Exception ignored) {}
        // Fire async refresh in background — cached response returns immediately
        try { self.asyncRefreshLastPushedAt(repoId); } catch (Exception ignored) {}
        Map<Long, double[]> statsMap = buildReviewStatsMap(List.of(repoId));
        double[] stats = statsMap.getOrDefault(repoId, new double[]{0, 0.0});
        return mapToRepoSummary(repo, (int) stats[0], stats[1]);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "reposByCourse", unless = "#result.isEmpty()")
    public List<RepoSummaryResponse> getApprovedReposByCourse(Long courseId) {
        List<GithubRepo> repos = githubRepoRepository.findByCourseIdAndActiveTrue(courseId);
        List<Long> repoIds = repos.stream().map(GithubRepo::getId).toList();
        Map<Long, double[]> statsMap = buildReviewStatsMap(repoIds);
        List<RepoSummaryResponse> responses = repos.stream()
                .map(repo -> {
                    double[] stats = statsMap.getOrDefault(repo.getId(), new double[]{0, 0.0});
                    return mapToRepoSummary(repo, (int) stats[0], stats[1]);
                })
                .sorted((a, b) -> {
                    // 1. newest lastPushedAt first
                    String da = a.lastPushedAt();
                    String db = b.lastPushedAt();
                    if (da != null && db != null) return db.compareTo(da);
                    if (db != null) return 1;
                    if (da != null) return -1;
                    // 2. newest approvedAt first
                    String aa = a.approvedAt();
                    String ab = b.approvedAt();
                    if (aa != null && ab != null) return ab.compareTo(aa);
                    if (ab != null) return 1;
                    if (aa != null) return -1;
                    // 3. highest id first
                    return Long.compare(b.id(), a.id());
                })
                .toList();
        // Fire async refresh for any repos needing GitHub data
        for (var repo : repos) {
            if (repo.getLastPushedAt() == null || repo.getLastPushedAt().isBlank()) {
                try { self.asyncRefreshLastPushedAt(repo.getId()); } catch (Exception ignored) {}
            }
        }
        return responses;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "allRepos", unless = "#result.isEmpty()")
    public List<RepoSummaryResponse> getAllApprovedRepos() {
        List<GithubRepo> repos = githubRepoRepository.findByActiveTrue();
        log.info("getAllApprovedRepos: found {} active repos", repos.size());
        List<Long> repoIds = repos.stream().map(GithubRepo::getId).toList();
        Map<Long, double[]> statsMap = buildReviewStatsMap(repoIds);
        List<RepoSummaryResponse> responses = repos.stream()
                .map(repo -> {
                    double[] stats = statsMap.getOrDefault(repo.getId(), new double[]{0, 0.0});
                    return mapToRepoSummary(repo, (int) stats[0], stats[1]);
                })
                .toList();
        // Async refresh in background
        for (var repo : repos) {
            if (repo.getLastPushedAt() == null || repo.getLastPushedAt().isBlank()) {
                try { self.asyncRefreshLastPushedAt(repo.getId()); } catch (Exception ignored) {}
            }
        }
        return responses;
    }

    @Transactional(readOnly = true)
    public List<RepoSummaryResponse> getApprovedReposByCourseAndTechStack(Long courseId, String techStack) {
        List<GithubRepo> repos = githubRepoRepository.findByCourseIdAndActiveTrueAndTechStack(courseId, techStack);
        List<Long> repoIds = repos.stream().map(GithubRepo::getId).toList();
        Map<Long, double[]> statsMap = buildReviewStatsMap(repoIds);
        return repos.stream()
                .map(repo -> {
                    double[] stats = statsMap.getOrDefault(repo.getId(), new double[]{0, 0.0});
                    return mapToRepoSummary(repo, (int) stats[0], stats[1]);
                })
                .toList();
    }

    // =====================================================================
    // WRITE METHODS — evict cache
    // =====================================================================

    @Transactional
    @CacheEvict(value = {"reposByCourse", "repoById", "allRepos"}, allEntries = true)
    public RepoSummaryResponse updateApprovedRepo(Long repoId, ApprovedRepoUpdateRequest request) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));

        if (request.displayName() != null) repo.setDisplayName(trimToNull(request.displayName()));
        if (request.description() != null) repo.setDescription(request.description().trim());
        if (request.githubUrl() != null) repo.setGithubUrl(trimToNull(request.githubUrl()));
        if (request.primaryLanguage() != null) repo.setPrimaryLanguage(trimToNull(request.primaryLanguage()));
        if (request.stars() != null) repo.setStars(Math.max(0, request.stars()));
        if (request.active() != null) repo.setActive(request.active());

        Course oldCourse = repo.getCourse();
        Set<TechStack> oldTechStacks = repo.getTechStacks() != null ? new java.util.LinkedHashSet<>(repo.getTechStacks()) : new java.util.LinkedHashSet<>();

        if (request.techStacks() != null) {
            repo.setTechStacks(resolveTechStacks(request.techStacks()));
        }

        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new NotFoundException("Course not found: " + request.courseId()));
            repo.setCourse(course);
            repo.setSubjectId(course.getMaMH());
        }

        repoEvaluationService.evaluateRepo(repo);
        GithubRepo saved = githubRepoRepository.save(repo);

        if (saved.getCourse() != null && !saved.getCourse().equals(oldCourse)) {
            try {
                studentNotificationService.notifyCourseSubscribers(saved, saved.getCourse());
            } catch (Exception e) {
                log.warn("Failed to notify course subscribers on update: {}", e.getMessage());
            }
        }

        if (saved.getTechStacks() != null) {
            for (TechStack ts : saved.getTechStacks()) {
                if (!oldTechStacks.contains(ts)) {
                    try {
                        studentNotificationService.notifyTechStackSubscribers(saved, ts);
                    } catch (Exception e) {
                        log.warn("Failed to notify tech stack subscribers on update: {}", e.getMessage());
                    }
                }
            }
        }

        Map<Long, double[]> statsMap = buildReviewStatsMap(List.of(saved.getId()));
        double[] stats = statsMap.getOrDefault(saved.getId(), new double[]{0, 0.0});
        RepoSummaryResponse response = mapToRepoSummary(saved, (int) stats[0], stats[1]);
        log.info("updateApprovedRepo: updated repo id={} active={}", repoId, request.active());
        return response;
    }

    @Transactional
    @CacheEvict(value = {"reposByCourse", "repoById", "allRepos"}, allEntries = true)
    public void deleteApprovedRepo(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));

        // Clean up user-facing dependents before soft-delete
        studentBookmarkRepository.deleteByTargetTypeAndTargetId("REPO", repoId);
        noteRepository.deleteByTargetTypeAndTargetId(NoteTargetType.REPO, repoId);
        repoVoteRepository.deleteByRepoId(repoId);
        repoReviewRepository.deleteByRepoId(repoId);

        repo.setActive(false);
        githubRepoRepository.save(repo);
        log.info("deleteApprovedRepo: deactivated repo id={} with cleaned dependents", repoId);
    }

    @Transactional
    @CacheEvict(value = {"reposByCourse", "repoById", "allRepos"}, allEntries = true)
    public RepoSummaryResponse syncApprovedRepo(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));

        RepoSlug slug = parseGithubSlug(repo.getGithubUrl());
        if (slug == null) {
            throw new BadRequestException("Invalid GitHub URL for repo: " + repoId);
        }

        // 1. Fetch Repository Metadata from GitHub
        JsonNode metadata = githubScanService.fetchRepoMetadata(slug.owner(), slug.name());
        if (metadata != null && !metadata.isMissingNode()) {
            if (metadata.path("description").asText(null) != null && (repo.getDescription() == null || repo.getDescription().isBlank())) {
                repo.setDescription(metadata.path("description").asText(null));
            }
            String lang = metadata.path("language").asText(null);
            if (lang != null && !lang.isBlank()) {
                repo.setPrimaryLanguage(lang);
            }
            if (!metadata.path("stargazers_count").isMissingNode()) {
                repo.setStars(metadata.path("stargazers_count").asInt(0));
            }
            String lastPushed = githubScanService.resolveLatestActivityAt(slug.owner(), slug.name(), metadata);
            if (lastPushed != null) {
                repo.setLastPushedAt(lastPushed);
            }
        }

        // 2. Fetch README and File Tree
        GithubScanService.RepositoryContext context = githubScanService.fetchRepositoryContext(slug.owner(), slug.name());
        if (context.readmeExcerpt() != null) {
            repo.setReadmeExcerpt(context.readmeExcerpt());
        }
        if (context.fileTree() != null) {
            repo.setFileTree(context.fileTree());
        }
        if (context.hasReadme() != null) {
            repo.setHasReadme(context.hasReadme());
        } else if (context.readmeExcerpt() != null) {
            repo.setHasReadme(true);
        }

        // 3. Resolve tech stack if empty
        if ((repo.getTechStacks() == null || repo.getTechStacks().isEmpty()) 
            && repo.getPrimaryLanguage() != null && !repo.getPrimaryLanguage().isBlank()) {
            repo.setTechStacks(resolveTechStacks(List.of(repo.getPrimaryLanguage())));
        }

        // 4. Re-evaluate
        repoEvaluationService.evaluateRepo(repo);

        GithubRepo saved = githubRepoRepository.save(repo);
        log.info("syncApprovedRepo: successfully synced metadata and re-evaluated repo id={} ({})", repoId, repo.getGithubUrl());
        Map<Long, double[]> statsMap = buildReviewStatsMap(List.of(saved.getId()));
        double[] stats = statsMap.getOrDefault(saved.getId(), new double[]{0, 0.0});
        return mapToRepoSummary(saved, (int) stats[0], stats[1]);
    }

    // =====================================================================
    // ASYNC GITHUB REFRESH — background HTTP calls
    // =====================================================================

    @Async
    @Transactional
    public void asyncTrackView(Long repoId) {
        try {
            githubRepoRepository.findById(repoId).ifPresent(repo -> {
                int current = repo.getViewCount() != null ? repo.getViewCount() : 0;
                repo.setViewCount(current + 1);
                githubRepoRepository.save(repo);
            });
        } catch (Exception e) {
            log.warn("asyncTrackView failed for repo id={}: {}", repoId, e.getMessage());
        }
    }

    @Async
    public void asyncRefreshLastPushedAt(Long repoId) {
        try {
            GithubRepo repo = githubRepoRepository.findById(repoId).orElse(null);
            if (repo == null) return;
            if (repo.getLastPushedAt() != null && !repo.getLastPushedAt().isBlank()) return;

            RepoSlug slug = parseGithubSlug(repo.getGithubUrl());
            if (slug == null) return;

            String lastPushedAt = githubScanService.fetchLatestActivityAt(slug.owner(), slug.name());
            if (lastPushedAt == null) return;

            repo.setLastPushedAt(lastPushedAt);
            githubRepoRepository.save(repo);
            // Evict cached course lists so they pick up the fresh lastPushedAt
            Cache cached = cacheManager.getCache("reposByCourse");
            if (cached != null) cached.clear();
            cached = cacheManager.getCache("allRepos");
            if (cached != null) cached.clear();
            log.info("asyncRefreshed lastPushedAt for repo id={} -> {}", repoId, lastPushedAt);
        } catch (Exception e) {
            log.warn("asyncRefreshLastPushedAt failed for repo id={}: {}", repoId, e.getMessage());
        }
    }

    // =====================================================================
    // SCHEDULED BATCH REFRESH — runs every 30 min
    // =====================================================================

    @Scheduled(fixedRate = 1_800_000) // 30 min
    public void batchRefreshStaleLastPushedAt() {
        List<GithubRepo> stale = githubRepoRepository.findStaleActiveRepos().stream()
                .filter(r -> r.getGithubUrl() != null && !r.getGithubUrl().isBlank())
                .toList();
        if (stale.isEmpty()) {
            log.debug("batchRefreshStaleLastPushedAt: no stale repos");
            return;
        }
        log.info("batchRefreshStaleLastPushedAt: refreshing {} repos", stale.size());
        for (GithubRepo repo : stale) {
            try {
                RepoSlug slug = parseGithubSlug(repo.getGithubUrl());
                if (slug == null) continue;
                String lastPushedAt = githubScanService.fetchLatestActivityAt(slug.owner(), slug.name());
                if (lastPushedAt != null) {
                    repo.setLastPushedAt(lastPushedAt);
                    githubRepoRepository.save(repo);
                }
                Thread.sleep(200); // rate-limit politeness
            } catch (Exception e) {
                log.warn("batchRefresh failed for repo id={}: {}", repo.getId(), e.getMessage());
            }
        }
        // Evict cached course/repo lists so they pick up fresh lastPushedAt values
        Cache cached = cacheManager.getCache("reposByCourse");
        if (cached != null) cached.clear();
        cached = cacheManager.getCache("allRepos");
        if (cached != null) cached.clear();
        log.info("batchRefreshStaleLastPushedAt: completed, evicted reposByCourse/allRepos caches");
    }

    // =====================================================================
    // DTO MAPPING
    // =====================================================================

    @Transactional(readOnly = true)
    public RepoSummaryResponse mapToRepoSummary(GithubRepo repo) {
        Map<Long, double[]> statsMap = buildReviewStatsMap(List.of(repo.getId()));
        double[] stats = statsMap.getOrDefault(repo.getId(), new double[]{0, 0.0});
        return mapToRepoSummary(repo, (int) stats[0], stats[1]);
    }

    public RepoSummaryResponse mapToRepoSummary(GithubRepo repo, int reviewCount, double averageRating) {
        Long courseId = null;
        String courseCode = null;
        String courseName = null;
        if (repo.getCourse() != null) {
            courseId = repo.getCourse().getId();
            courseCode = repo.getCourse().getMaMH();
            courseName = repo.getCourse().getTenMH();
        }
        return new RepoSummaryResponse(
                repo.getId(),
                repo.getDisplayName(),
                repo.getDescription(),
                repo.getGithubUrl(),
                repo.getPrimaryLanguage(),
                repo.getStars() != null ? repo.getStars() : 0,
                repo.getTechStacks().stream()
                        .map(ts -> new TechStackResponse(ts.getId(), ts.getName()))
                        .toList(),
                courseId,
                courseCode,
                courseName,
                repo.getReadmeExcerpt(),
                repo.getFileTree(),
                repo.getHasReadme(),
                repo.getLastPushedAt(),
                repo.getApprovedAt() != null ? repo.getApprovedAt().toString() : null,
                repo.getRepoType(),
                repo.getUsefulnessRating(),
                repo.getUsefulnessScore(),
                repo.getReadyToUseLevel(),
                reviewCount,
                averageRating
        );
    }

    // =====================================================================
    // REVIEW STATS HELPERS
    // =====================================================================

    private Map<Long, double[]> buildReviewStatsMap(List<Long> repoIds) {
        if (repoIds == null || repoIds.isEmpty()) return Map.of();
        return repoReviewRepository.countAndAverageByRepoIds(repoIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> new double[]{((Number) row[1]).longValue(), ((Number) row[2]).doubleValue()}
                ));
    }

    // =====================================================================
    // HELPERS
    // =====================================================================

    private RepoSlug parseGithubSlug(String githubUrl) {
        if (githubUrl == null || githubUrl.isBlank()) return null;
        java.util.regex.Matcher matcher = java.util.regex.Pattern
            .compile("github\\.com/([^/]+)/([^/?#]+)", java.util.regex.Pattern.CASE_INSENSITIVE)
            .matcher(githubUrl);
        if (!matcher.find()) return null;
        String name = matcher.group(2).replaceAll("\\.git$", "");
        return new RepoSlug(matcher.group(1), name);
    }

    private record RepoSlug(String owner, String name) {}

    public Set<TechStack> resolveTechStacks(List<String> stackNames) {
        Set<TechStack> stacks = new LinkedHashSet<>();
        Set<String> seen = new LinkedHashSet<>();
        for (String rawName : stackNames) {
            String name = trimToNull(rawName);
            if (name == null) continue;
            String key = name.toLowerCase(Locale.ROOT);
            if (!seen.add(key)) continue;
            TechStack stack = techStackRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> techStackRepository.save(TechStack.builder().name(name).build()));
            stacks.add(stack);
        }
        return stacks;
    }

    private String trimToNull(String value) {
        String trimmed = value == null ? null : value.trim();
        return trimmed == null || trimmed.isEmpty() ? null : trimmed;
    }
}
