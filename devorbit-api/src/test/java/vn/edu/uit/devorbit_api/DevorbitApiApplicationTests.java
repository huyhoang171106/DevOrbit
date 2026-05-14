package vn.edu.uit.devorbit_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.edu.uit.devorbit_api.repository.AdminUserRepository;
import vn.edu.uit.devorbit_api.repository.CourseRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "app.jwt.secret=test-jwt-secret-key-that-is-long-enough",
    "app.github.token=test-github-token"
})
class DevorbitApiApplicationTests {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void shouldSeedAtLeastOneCourse() {
        assertThat(courseRepository.count()).isGreaterThan(0);
    }

    @Test
    void shouldSeedAdminUser() {
        assertThat(adminUserRepository.findByUsername("admin")).isPresent();
    }
}
