package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.repository.RepoCandidateRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "app.jwt.secret=test-secret-key-test-secret-key",
    "app.github.token=test-token"
})
class RepoCandidateServiceTest {

    @Autowired
    private RepoCandidateRepository repoCandidateRepository;

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
}
