package vn.edu.uit.devorbit_api.dto.admin;

public record AdminCourseUpsertRequest(
    String code,
    String name,
    Integer credits,
    Integer lectureHours,
    Integer practiceHours,
    String subjectType,
    String description
) {}
