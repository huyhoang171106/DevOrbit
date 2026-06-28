package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GithubAutoApprovalServiceTest {
    @Test
    void recognizesUitAsAStandaloneTokenCaseInsensitively() {
        assertThat(GithubAutoApprovalService.containsUit("Đồ án UIT", "other")).isTrue();
        assertThat(GithubAutoApprovalService.containsUit("uit-se-project")).isTrue();
    }

    @Test
    void doesNotMatchUitInsideAnUnrelatedWord() {
        assertThat(GithubAutoApprovalService.containsUit("suite", "build with intuition")).isFalse();
    }
}
