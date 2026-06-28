package vn.edu.uit.devorbit_api.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.service.CourseService;
import vn.edu.uit.devorbit_api.service.GithubRepoService;
import vn.edu.uit.devorbit_api.service.KnowledgeGraphService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupCacheWarmup {

    private final CourseService courseService;
    private final GithubRepoService githubRepoService;
    private final KnowledgeGraphService knowledgeGraphService;
    private final CacheManager cacheManager;

    @EventListener(ApplicationReadyEvent.class)
    public void warmUp() {
        long startedAt = System.currentTimeMillis();
        int warmedCourseRepos = 0;
        int warmedRepos = 0;

        try {
            List<CourseSummaryResponse> courses = courseService.getActiveCourseSummaries();
            List<RepoSummaryResponse> repos = githubRepoService.getAllApprovedRepos();
            warmedRepos = repos.size();
            Map<Long, List<RepoSummaryResponse>> reposByCourse = repos.stream()
                .filter(repo -> repo.courseId() != null)
                .collect(Collectors.groupingBy(RepoSummaryResponse::courseId));
            Cache reposByCourseCache = cacheManager.getCache("reposByCourse");
            if (reposByCourseCache != null) {
                reposByCourse.forEach((courseId, courseRepos) ->
                    reposByCourseCache.put(courseId, sortCourseRepos(courseRepos))
                );
                warmedCourseRepos = reposByCourse.size();
            }

            knowledgeGraphService.getGraph();
            log.info(
                "Startup cache warm-up completed in {} ms (courses={}, courseRepoLists={}, repos={})",
                System.currentTimeMillis() - startedAt,
                courses.size(),
                warmedCourseRepos,
                warmedRepos
            );
        } catch (RuntimeException e) {
            log.warn("Startup cache warm-up stopped early after {} ms: {}",
                System.currentTimeMillis() - startedAt, e.getMessage(), e);
        }
    }

    private List<RepoSummaryResponse> sortCourseRepos(List<RepoSummaryResponse> repos) {
        return repos.stream()
            .sorted((a, b) -> {
                String lastPushedA = a.lastPushedAt();
                String lastPushedB = b.lastPushedAt();
                if (lastPushedA != null && lastPushedB != null) return lastPushedB.compareTo(lastPushedA);
                if (lastPushedB != null) return 1;
                if (lastPushedA != null) return -1;

                String approvedA = a.approvedAt();
                String approvedB = b.approvedAt();
                if (approvedA != null && approvedB != null) return approvedB.compareTo(approvedA);
                if (approvedB != null) return 1;
                if (approvedA != null) return -1;
                return Long.compare(b.id(), a.id());
            })
            .toList();
    }
}
