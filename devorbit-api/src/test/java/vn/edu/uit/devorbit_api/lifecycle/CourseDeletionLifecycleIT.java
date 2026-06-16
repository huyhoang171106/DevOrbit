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

        long studentId = ((Number) em.createNativeQuery("SELECT id FROM student_users WHERE student_code = 'LIFE_TEST_001'")
                .getSingleResult()).longValue();

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

        // Also create a soft-deleted (inactive) repo
        em.createNativeQuery("INSERT INTO github_repos (repo_name, github_url, course_id, is_active) VALUES ('inactive-repo', 'https://github.com/test/inactive', ?, false)")
                .setParameter(1, courseId).executeUpdate();

        em.createNativeQuery("INSERT INTO repo_candidates (course_id, github_url, github_name, status, created_at) VALUES (?, 'https://github.com/test/candidate', 'test-candidate', 'NEW', CURRENT_TIMESTAMP)")
                .setParameter(1, courseId).executeUpdate();

        // Get generated repo IDs
        long repoId1 = ((Number) em.createNativeQuery("SELECT id FROM github_repos WHERE repo_name = 'test-repo'").getSingleResult()).longValue();
        long repoId2 = ((Number) em.createNativeQuery("SELECT id FROM github_repos WHERE repo_name = 'inactive-repo'").getSingleResult()).longValue();

        // Create StudentBookmarks for course and repo
        em.createNativeQuery("INSERT INTO student_bookmarks (student_id, target_type, target_id, title, url, created_at) VALUES (?, 'COURSE', ?, 'Course Bookmark', '/courses/' || ?, CURRENT_TIMESTAMP)")
                .setParameter(1, studentId).setParameter(2, courseId).setParameter(3, courseId).executeUpdate();

        em.createNativeQuery("INSERT INTO student_bookmarks (student_id, target_type, target_id, title, url, created_at) VALUES (?, 'REPO', ?, 'Repo Bookmark', '/repos/' || ?, CURRENT_TIMESTAMP)")
                .setParameter(1, studentId).setParameter(2, repoId1).setParameter(3, repoId1).executeUpdate();

        // Create Notes for course and repo
        em.createNativeQuery("INSERT INTO notes (student_id, title, content_markdown, target_type, target_id, created_at, updated_at) VALUES (?, 'Course Note', 'Course note content', 'COURSE', ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .setParameter(1, studentId).setParameter(2, courseId).executeUpdate();

        em.createNativeQuery("INSERT INTO notes (student_id, title, content_markdown, target_type, target_id, created_at, updated_at) VALUES (?, 'Repo Note', 'Repo note content', 'REPO', ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .setParameter(1, studentId).setParameter(2, repoId1).executeUpdate();

        // Create RepoVote and RepoReview for the active repo
        em.createNativeQuery("INSERT INTO repo_votes (repo_id, student_id, vote_value, created_at) VALUES (?, ?, 1, CURRENT_TIMESTAMP)")
                .setParameter(1, repoId1).setParameter(2, studentId).executeUpdate();

        em.createNativeQuery("INSERT INTO repo_reviews (repo_id, student_id, rating, comment, created_at, updated_at) VALUES (?, ?, 4, 'Good repo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .setParameter(1, repoId1).setParameter(2, studentId).executeUpdate();

        // Create CourseReview
        em.createNativeQuery("INSERT INTO course_reviews (course_id, student_id, rating, comment, created_at, updated_at) VALUES (?, ?, 5, 'Great course', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
                .setParameter(1, courseId).setParameter(2, studentId).executeUpdate();

        // Create ChatChannel for this course
        em.createNativeQuery("INSERT INTO chat_channels (channel_id, name, type, reference_id, active, created_at) VALUES ('course-life-test-101', 'Lifecycle Test Course', 'COURSE', ?, true, CURRENT_TIMESTAMP)")
                .setParameter(1, String.valueOf(courseId)).executeUpdate();

        // Get generated channel ID and create CommunityMessage
        Long channelId = (Long) em.createNativeQuery("SELECT id FROM chat_channels WHERE channel_id = 'course-life-test-101'").getSingleResult();
        em.createNativeQuery("INSERT INTO community_messages (channel_id, student_id, content, created_at) VALUES (?, ?, 'Test message', CURRENT_TIMESTAMP)")
                .setParameter(1, channelId).setParameter(2, studentId).executeUpdate();

        em.flush();
    }

    @Test
    @DisplayName("GREEN: Delete course removes all dependents (no orphans)")
    void deleteCourse_removesAllDependents() {
        // Pre-conditions: all dependents exist
        assertThat(courseRepository.findById(courseId)).isPresent();
        assertThat(countBySql("course_tutorials", "course_id = " + courseId)).isEqualTo(1);
        assertThat(countBySql("course_youtube_playlists", "course_id = " + courseId)).isEqualTo(1);
        assertThat(countBySql("course_articles", "course_id = " + courseId)).isEqualTo(1);
        assertThat(countBySql("course_relationships",
                "course_id = " + courseId + " OR related_course_id = " + courseId)).isEqualTo(1);
        assertThat(countBySql("github_repos", "course_id = " + courseId)).isEqualTo(2);
        assertThat(countBySql("repo_candidates", "course_id = " + courseId)).isEqualTo(1);

        // Also verify new dependents exist
        assertThat(countBySql("student_bookmarks", "target_type = 'COURSE' AND target_id = " + courseId))
                .as("Course bookmark exists").isEqualTo(1);
        assertThat(countBySql("student_bookmarks",
                "target_type = 'REPO' AND target_id IN (SELECT id FROM github_repos WHERE course_id = " + courseId + ")"))
                .as("Repo bookmark exists").isEqualTo(1);
        assertThat(countBySql("notes", "target_type = 'COURSE' AND target_id = " + courseId))
                .as("Course note exists").isEqualTo(1);
        assertThat(countBySql("notes",
                "target_type = 'REPO' AND target_id IN (SELECT id FROM github_repos WHERE course_id = " + courseId + ")"))
                .as("Repo note exists").isEqualTo(1);
        assertThat(countBySql("repo_votes",
                "repo_id IN (SELECT id FROM github_repos WHERE course_id = " + courseId + ")"))
                .as("Repo vote exists").isEqualTo(1);
        assertThat(countBySql("course_reviews", "course_id = " + courseId))
                .as("Course review exists").isEqualTo(1);
        assertThat(countBySql("chat_channels", "reference_id = '" + courseId + "'"))
                .as("Chat channel exists").isEqualTo(1);
        assertThat(countBySql("community_messages",
                "channel_id IN (SELECT id FROM chat_channels WHERE reference_id = '" + courseId + "')"))
                .as("Community message exists").isEqualTo(1);

        // Act: delete the course
        courseService.deleteCourse(courseId);

        // Verify: course is gone
        assertThat(courseRepository.findById(courseId)).isEmpty();

        // Verify: ALL direct dependents are removed
        assertThat(countBySql("course_tutorials", "course_id = " + courseId))
                .as("CourseTutorials should be removed").isEqualTo(0);
        assertThat(countBySql("course_youtube_playlists", "course_id = " + courseId))
                .as("CourseYoutubePlaylists should be removed").isEqualTo(0);
        assertThat(countBySql("course_articles", "course_id = " + courseId))
                .as("CourseArticles should be removed").isEqualTo(0);
        assertThat(countBySql("course_relationships",
                "course_id = " + courseId + " OR related_course_id = " + courseId))
                .as("CourseRelationships should be removed").isEqualTo(0);
        assertThat(countBySql("github_repos", "course_id = " + courseId))
                .as("GithubRepos should be removed").isEqualTo(0);
        assertThat(countBySql("repo_candidates", "course_id = " + courseId))
                .as("RepoCandidates should be removed").isEqualTo(0);
        assertThat(countBySql("course_reviews", "course_id = " + courseId))
                .as("CourseReviews should be removed").isEqualTo(0);

        // Verify: NEW cleanup paths also work
        assertThat(countBySql("student_bookmarks", "target_type = 'COURSE' AND target_id = " + courseId))
                .as("Course bookmarks should be removed").isEqualTo(0);
        assertThat(countBySql("student_bookmarks",
                "target_type = 'REPO' AND target_id IN (SELECT id FROM github_repos WHERE course_id = " + courseId + ")"))
                .as("Repo bookmarks should be removed").isEqualTo(0);
        assertThat(countBySql("notes", "target_type = 'COURSE' AND target_id = " + courseId))
                .as("Course notes should be removed").isEqualTo(0);
        assertThat(countBySql("notes",
                "target_type = 'REPO' AND target_id IN (SELECT id FROM github_repos WHERE course_id = " + courseId + ")"))
                .as("Repo notes should be removed").isEqualTo(0);
        assertThat(countBySql("repo_votes",
                "repo_id IN (SELECT id FROM github_repos WHERE course_id = " + courseId + ")"))
                .as("Repo votes should be removed").isEqualTo(0);
        assertThat(countBySql("repo_reviews",
                "repo_id IN (SELECT id FROM github_repos WHERE course_id = " + courseId + ")"))
                .as("Repo reviews should be removed").isEqualTo(0);
        assertThat(countBySql("chat_channels", "reference_id = '" + courseId + "' AND active = true"))
                .as("Course chat channels should be deactivated (not removed) when messages exist").isEqualTo(0);
        assertThat(countBySql("chat_channels", "reference_id = '" + courseId + "' AND active = false"))
                .as("Course chat channel should exist but be inactive").isEqualTo(1);
        // Community messages preserved because channel is soft-deleted (active=false)
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
