package vn.edu.uit.devorbit_api.dto.admin;

import vn.edu.uit.devorbit_api.entity.CourseRelationType;
import java.time.LocalDateTime;

public record CourseRelationshipResponse(
    Long id,
    Long courseId,
    String courseCode,
    String courseName,
    String courseNameEn,
    Long relatedCourseId,
    String relatedCourseCode,
    String relatedCourseName,
    String relatedCourseNameEn,
    CourseRelationType relationType,
    LocalDateTime createdAt
) {}
