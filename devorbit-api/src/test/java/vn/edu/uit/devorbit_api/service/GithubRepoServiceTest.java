package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GithubRepoServiceTest {
    @Mock
    private GithubRepoRepository githubRepoRepository;

    @Mock
    private TechStackRepository techStackRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private GithubScanService githubScanService;

    @Test
    void refreshesMissingLastPushedAtWhenRepoDetailIsOpened() {
        GithubRepo repo = new GithubRepo();
        repo.setId(1L);
        repo.setRepoName("repo");
        repo.setDisplayName("repo");
        repo.setGithubUrl("https://github.com/owner/repo");
        repo.setActive(true);

        when(githubRepoRepository.findById(1L)).thenReturn(Optional.of(repo));
        when(githubScanService.fetchLatestActivityAt("owner", "repo")).thenReturn("2026-04-20T10:00:00Z");

        GithubRepoService service = new GithubRepoService(
            githubRepoRepository,
            techStackRepository,
            courseRepository,
            githubScanService
        );

        RepoSummaryResponse response = service.getApprovedRepoById(1L);

        assertThat(response.lastPushedAt()).isEqualTo("2026-04-20T10:00:00Z");
        verify(githubRepoRepository).save(repo);
    }
}
