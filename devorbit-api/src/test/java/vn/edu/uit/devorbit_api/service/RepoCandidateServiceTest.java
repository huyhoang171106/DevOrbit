package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.GithubScanRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.repository.RepoCandidateRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
    "app.jwt.secret=test-secret-key-that-is-long-enough-32",
    "app.github.token=test-token"
})
@Transactional
class RepoCandidateServiceTest {

    @Autowired
    private RepoCandidateRepository repoCandidateRepository;

    @MockitoBean
    private GithubScanService githubScanService;

    @Test
    void shouldStoreNewCandidateStatus() {
        RepoCandidate candidate = RepoCandidate.builder()
            .githubUrl("https://github.com/example/repo")
            .githubOwner("example")
            .githubName("repo")
            .status(RepoCandidateStatus.NEW)
            .build();

        RepoCandidate saved = repoCandidateRepository.save(candidate);
        assertThat(saved.getStatus()).isEqualTo(RepoCandidateStatus.NEW);
    }

    @Test
    void shouldPersistNewCandidatesFromScan() {
        GithubScanRequest request = new GithubScanRequest(1L, "SE101 UIT");

        when(githubScanService.scan(any())).thenReturn(List.of(
            new RepoCandidateResponse(1L, "example", "repo", "https://github.com/example/repo", "NEW")
        ));

        List<RepoCandidateResponse> results = githubScanService.scan(request);

        assertThat(results).hasSize(1);
    }
}
