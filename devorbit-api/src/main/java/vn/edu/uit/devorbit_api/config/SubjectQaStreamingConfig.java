package vn.edu.uit.devorbit_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Thread pool for streaming SSE responses from SubjectQaService.
 * Prevents blocking the servlet container thread during long-running LLM streaming.
 */
@Configuration
public class SubjectQaStreamingConfig {

    @Bean(name = "subjectQaStreamExecutor")
    public Executor subjectQaStreamExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("subject-qa-stream-");
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(0);
        executor.initialize();
        return executor;
    }
}
