package vn.edu.uit.devorbit_api.dto.student;

public record SemesterCourseResponse(
    Long id,
    Long courseId,
    String courseCode,
    String courseName,
    String createdAt
) {}
