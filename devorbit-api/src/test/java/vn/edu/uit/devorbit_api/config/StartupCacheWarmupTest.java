package vn.edu.uit.devorbit_api.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.service.CourseService;
import vn.edu.uit.devorbit_api.service.GithubRepoService;
import vn.edu.uit.devorbit_api.service.KnowledgeGraphService;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StartupCacheWarmupTest {

    @Test
    void warmsGlobalAndPerCourseCaches() {
        CourseService courseService = mock(CourseService.class);
        GithubRepoService repoService = mock(GithubRepoService.class);
        KnowledgeGraphService graphService = mock(KnowledgeGraphService.class);
        CourseSummaryResponse course = mock(CourseSummaryResponse.class);
        RepoSummaryResponse repo = mock(RepoSummaryResponse.class);
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager("reposByCourse");

        when(course.id()).thenReturn(11L);
        when(repo.id()).thenReturn(22L);
        when(repo.courseId()).thenReturn(11L);
        when(courseService.getActiveCourseSummaries()).thenReturn(List.of(course));
        when(repoService.getAllApprovedRepos()).thenReturn(List.of(repo));

        new StartupCacheWarmup(courseService, repoService, graphService, cacheManager).warmUp();

        verify(courseService).getActiveCourseSummaries();
        verify(repoService).getAllApprovedRepos();
        verify(graphService).getGraph();
        assertNotNull(cacheManager.getCache("reposByCourse").get(11L, List.class));
    }
}
