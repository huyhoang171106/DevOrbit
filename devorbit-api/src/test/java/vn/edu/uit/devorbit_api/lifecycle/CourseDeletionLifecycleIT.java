package vn.edu.uit.devorbit_api.lifecycle;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.repository.*;
import vn.edu.uit.devorbit_api.service.CourseService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Lifecycle integration test for Course deletion.
 *
 * TDD Phase: RED — Proves deleteCourse() leaves orphaned dependents.
 * Uses native SQL queries for verification to avoid transient-entity issues.
 */
@SpringBootTest
@ActiveProfiles("lifecycle")
@Transactional
class CourseDeletionLifecycleIT {

    @Autowired private CourseService courseService;
    @Autowired private CourseRepository courseRepository;
    @Autowired private StudentUserRepository studentUserRepository;

    @PersistenceContext
    private EntityManager em;

    private Long courseId;
    private Long relatedCourseId;

    private long countBySql(String table, String where) {
        return ((Number) em.createNativeQuery("SELECT COUNT(*) FROM " + table + " WHERE " + where)
                .getSingleResult()).longValue();
    }

    @BeforeEach
    void setUp() {
        studentUserRepository.saveAndFlush(StudentUser.builder()
                .studentCode("LIFE_TEST_001")
                .fullName("Lifecycle Test Student")
                .email("lifecycle@test.com")
                .passwordHash("hashed-password")
                .build());

        Course course = courseRepository.saveAndFlush(Course.builder()
                .maMH("LIFE_TEST_101")
                .tenMH("Lifecycle Test Course")
                .loaiMonHoc("CHUYEN_NGANH")
                .soTC(3)
                .isOpen(true)
                .build());
        courseId = course.getId();

        Course relatedCourse = courseRepository.saveAndFlush(Course.builder()
                .maMH("LIFE_TEST_201")
                .tenMH("Related Course")
                .loaiMonHoc("CHUYEN_NGANH")
                .soTC(3)
                .isOpen(true)
                .build());
        relatedCourseId = relatedCourse.getId();

        // Create dependents via native SQL to avoid entity management issues
        em.createNativeQuery("INSERT INTO course_tutorials (course_id, title, url, type, created_at) VALUES (?, 'Tutorial', 'https://example.com', 'guide', CURRENT_TIMESTAMP)")
                .setParameter(1, courseId).executeUpdate();

        em.createNativeQuery("INSERT INTO course_youtube_playlists (course_id, title, url, created_at) VALUES (?, 'Playlist', 'https://youtube.com', CURRENT_TIMESTAMP)")
                .setParameter(1, courseId).executeUpdate();

        em.createNativeQuery("INSERT INTO course_articles (course_id, title, url, created_at) VALUES (?, 'Article', 'https://example.com', CURRENT_TIMESTAMP)")
                .setParameter(1, courseId).executeUpdate();

        em.createNativeQuery("INSERT INTO course_relationships (course_id, related_course_id, relation_type, created_at) VALUES (?, ?, 'PREREQUISITE', CURRENT_TIMESTAMP)")
                .setParameter(1, courseId).setParameter(2, relatedCourseId).executeUpdate();

        em.createNativeQuery("INSERT INTO github_repos (repo_name, github_url, course_id, is_active) VALUES ('test-repo', 'https://github.com/test/repo', ?, true)")
                .setParameter(1, courseId).executeUpdate();

        // Also create a soft-deleted (inactive) repo to test the fix
        em.createNativeQuery("INSERT INTO github_repos (repo_name, github_url, course_id, is_active) VALUES ('inactive-repo', 'https://github.com/test/inactive', ?, false)")
                .setParameter(1, courseId).executeUpdate();

        em.createNativeQuery("INSERT INTO repo_candidates (course_id, github_url, github_name, status, created_at) VALUES (?, 'https://github.com/test/candidate', 'test-candidate', 'NEW', CURRENT_TIMESTAMP)")
                .setParameter(1, courseId).executeUpdate();

        em.flush();
    }

    @Test
    @DisplayName("RED: Delete course leaves orphaned dependents (proves the bug)")
    void deleteCourse_leavesOrphanedDependents_provesBug() {
        // Pre-conditions: all dependents exist
        assertThat(courseRepository.findById(courseId)).isPresent();
        assertThat(countBySql("course_tutorials", "course_id = " + courseId)).isEqualTo(1);
        assertThat(countBySql("course_youtube_playlists", "course_id = " + courseId)).isEqualTo(1);
        assertThat(countBySql("course_articles", "course_id = " + courseId)).isEqualTo(1);
        assertThat(countBySql("course_relationships", "course_id = " + courseId)).isEqualTo(1);
        assertThat(countBySql("github_repos", "course_id = " + courseId)).isEqualTo(2);
        assertThat(countBySql("repo_candidates", "course_id = " + courseId)).isEqualTo(1);

        // Act + Assert: After fix, deleteCourse() should succeed without FK violation
        courseService.deleteCourse(courseId);

        // Verify: course is gone
        assertThat(courseRepository.findById(courseId)).isEmpty();

        // GREEN: All dependents should be cleaned up
        assertThat(countBySql("course_tutorials", "course_id = " + courseId))
                .as("CourseTutorials should be removed").isEqualTo(0);
        assertThat(countBySql("course_youtube_playlists", "course_id = " + courseId))
                .as("CourseYoutubePlaylists should be removed").isEqualTo(0);
        assertThat(countBySql("course_articles", "course_id = " + courseId))
                .as("CourseArticles should be removed").isEqualTo(0);
        assertThat(countBySql("course_relationships", "course_id = " + courseId))
                .as("CourseRelationships should be removed").isEqualTo(0);
        assertThat(countBySql("github_repos", "course_id = " + courseId))
                .as("GithubRepos should be removed").isEqualTo(0);
        assertThat(countBySql("repo_candidates", "course_id = " + courseId))
                .as("RepoCandidates should be removed").isEqualTo(0);
        assertThat(countBySql("course_reviews", "course_id = " + courseId))
                .as("CourseReviews should be removed").isEqualTo(0);
    }

    @Test
    @DisplayName("Related course survives when primary course is deleted")
    void deleteCourse_doesNotDeleteRelatedCourse() {
        courseService.deleteCourse(courseId);
        assertThat(courseRepository.findById(relatedCourseId)).isPresent();
    }

    @Test
    @DisplayName("Delete non-existent course throws NotFoundException")
    void deleteCourse_nonExistent_throwsNotFound() {
        assertThatThrownBy(() -> courseService.deleteCourse(999999L))
                .isInstanceOf(vn.edu.uit.devorbit_api.exception.NotFoundException.class);
    }
}
