package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;
import java.util.UUID;

public record SyllabusDetailsResponse(
    SyllabusDto syllabus,
    List<ObjectiveDto> objectives,
    List<OutcomeDto> outcomes,
    List<SessionDto> sessions,
    List<AssessmentDto> assessments,
    List<String> references,
    List<String> tools
) {
    public record SyllabusDto(
        UUID id,
        UUID sourceId,
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
        String description
    ) {}

    public record ObjectiveDto(
        UUID id,
        String courseCode,
        String description,
        com.fasterxml.jackson.databind.JsonNode outcomeRefs
    ) {}

    public record OutcomeDto(
        UUID id,
        String courseCode,
        String outcomeCode,
        String description
    ) {}

    public record SessionDto(
        UUID id,
        String courseCode,
        UUID sourceId,
        String sessionNo,
        String sessionType,
        String topic,
        String activities,
        String assessmentComponent
    ) {}

    public record AssessmentDto(
        UUID id,
        String courseCode,
        String componentCode,
        String description,
        Integer weightPercent
    ) {}
}
