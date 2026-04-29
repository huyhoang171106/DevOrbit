package vn.edu.uit.devorbit_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.edu.uit.devorbit_api.repository.CourseRepository;

@SpringBootTest(properties = {
    "app.jwt.secret=test-jwt-secret",
    "app.github.token=test-github-token"
})
class DevorbitApiApplicationTests {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldSeedAtLeastOneCourse() {
        org.assertj.core.api.Assertions.assertThat(courseRepository.count()).isGreaterThan(0);
    }
}
