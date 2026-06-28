package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class WeeklyGithubScanSchedulerTest {
    @Test
    void runsAtThreeUtcEverySaturday() throws Exception {
        Method method = WeeklyGithubScanScheduler.class.getMethod("scanAndAutoApprove");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        assertThat(scheduled.cron()).isEqualTo("0 0 3 * * SAT");
        assertThat(scheduled.zone()).isEqualTo("UTC");
    }
}
