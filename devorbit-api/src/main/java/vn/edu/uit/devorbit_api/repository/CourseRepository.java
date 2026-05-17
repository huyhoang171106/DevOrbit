package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.dto.publicapi.CourseSummaryResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByMaMH(String maMH);

    @Query("""
            SELECT cr.course FROM CourseRelationship cr
            WHERE cr.relatedCourse.id = :courseId AND cr.relationType = 'PREREQUISITE'
            """)
    List<Course> findDownstreamCourses(@Param("courseId") Long courseId);

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
}
