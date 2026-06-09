package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;

public record ExtractedSyllabusFacts(
    String courseCode,
    String courseNameVi,
    String courseNameEn,
    Integer credits,
    Integer theoryHours,
    Integer practiceHours,
    Integer selfStudyHours,
    String prerequisite,
    String previousCourse,
    String department,
    String description,
    List<ObjectiveDto> objectives,
    List<OutcomeDto> outcomes,
    List<SessionDto> theorySessions,
    List<SessionDto> practiceSessions,
    List<AssessmentDto> assessments,
    List<String> references,
    List<String> tools
) {
    public record ObjectiveDto(
        String description,
        List<String> outcomeRefs
    ) {}

    public record OutcomeDto(
        String code,
        String description
    ) {}

    public record SessionDto(
        String sessionNo,
        String topic,
        String activities,
        String assessmentComponent
    ) {}

    public record AssessmentDto(
        String componentCode,
        String description,
        Integer weightPercent
    ) {}
}
