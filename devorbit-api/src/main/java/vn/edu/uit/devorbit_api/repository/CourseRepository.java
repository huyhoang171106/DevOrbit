package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository = the DATA ACCESS LAYER.
 *
 * WHAT IT DOES:
 * This interface tells Spring Data JPA how to read/write Course data from the
 * "courses" database table. Spring generates the actual SQL at runtime.
 *
 * HOW IT WORKS (for beginners):
 * - Extending JpaRepository<Course, Long> gives us free methods:
 *     findAll(), findById(), save(), delete(), count()
 * - Custom query methods (like findByMaMH) are auto-implemented by Spring
 *   based on the method name pattern.
 * - Complex queries use @Query with JPQL (Java Persistence Query Language)
 *   which looks like SQL but works with Java objects.
 *
 * REQUEST FLOW:
 *   Controller → Service → Repository → Database
 *   (HTTP input)  (business logic)  (data access)  (PostgreSQL)
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Find a course by its unique course code.
     * Spring Data JPA auto-implements this from the method name!
     * The pattern: findBy + [FieldName]
     *
     * Usage example: courseRepository.findByMaMH("SE101")
     * Generated SQL: SELECT * FROM courses WHERE mamh = ?
     */
    Optional<Course> findByMaMH(String maMH);

    /** Batch fetch courses by multiple codes — replaces N+1 loop */
    List<Course> findByMaMHIn(Collection<String> maMHs);

    /**
     * Find all courses that have this course as a prerequisite.
     * Uses a JPQL query (looks like SQL, works with Java objects).
     *
     * The CourseRelationship table connects courses to their prerequisites.
     * Example: if SE201 has prerequisite SE101, then:
     *   findDownstreamCourses(SE101.id) returns [SE201]
     */
    @Query("""
            SELECT cr.course FROM CourseRelationship cr
            WHERE cr.relatedCourse.id = :courseId AND cr.relationType = 'PREREQUISITE'
            """)
    List<Course> findDownstreamCourses(@Param("courseId") Long courseId);

    /**
     * Get ALL courses with their GitHub repo count, sorted by most repos first.
     *
     * WHAT THIS DOES:
     * 1. Takes every Course (c)
     * 2. LEFT JOINs with GithubRepo (r) — so courses with ZERO repos still appear
     * 3. Filters to only ACTIVE repos (r.active = true)
     * 4. GROUPs by all course fields (necessary for COUNT aggregation)
     * 5. COUNTs how many repos each course has
     * 6. ORDERS by count descending (courses with most repos appear first)
     *
     * The result is a List<CourseSummaryResponse> — a DTO that contains
     * only the fields the frontend needs (not the full Course entity).
     *
     * NOTE: This returns ALL courses (both active and inactive).
     * The public endpoint might want only "active" courses in the future.
     */
    @Query("""
            SELECT new vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse(
                c.id, c.maMH, c.tenMH, c.description, COUNT(r), c.semester, c.soTC, c.loaiMonHoc, c.managementUnit
            )
            FROM Course c
            LEFT JOIN GithubRepo r ON r.course.id = c.id AND r.active = true
            GROUP BY c.id, c.maMH, c.tenMH, c.description, c.semester, c.soTC, c.loaiMonHoc, c.managementUnit
            ORDER BY COUNT(r) DESC
            """)
    List<CourseSummaryResponse> findAllWithRepoCountSortedByRepoCount();

    /** Get active courses with their GitHub repo count, sorted by most repos first. */
    @Query("""
            SELECT new vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse(
                c.id, c.maMH, c.tenMH, c.description, COUNT(r), c.semester, c.soTC, c.loaiMonHoc, c.managementUnit
            )
            FROM Course c
            LEFT JOIN GithubRepo r ON r.course.id = c.id AND r.active = true
            WHERE c.isOpen = true
            GROUP BY c.id, c.maMH, c.tenMH, c.description, c.semester, c.soTC, c.loaiMonHoc, c.managementUnit
            ORDER BY COUNT(r) DESC
            """)
    List<CourseSummaryResponse> findActiveWithRepoCountSortedByRepoCount();
}
