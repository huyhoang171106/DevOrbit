package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO containing the full syllabus details for a course.
 * Used by GET /api/knowledge/syllabus/{courseCode}.
 * Combines syllabus metadata with its nested objectives, outcomes, sessions, and assessments.
 *
 * @param syllabus    Core syllabus information (course name, credits, hours, etc.).
 * @param objectives  List of learning objectives with outcome references.
 * @param outcomes    List of program learning outcomes.
 * @param sessions    List of theory and practice sessions.
 * @param assessments List of assessment components with weights.
 * @param references  List of textbook/reference titles.
 * @param tools       List of tools/software used.
 */
public record SyllabusDetailsResponse(
    SyllabusDto syllabus,
    List<ObjectiveDto> objectives,
    List<OutcomeDto> outcomes,
    List<SessionDto> sessions,
    List<AssessmentDto> assessments,
    List<String> references,
    List<String> tools
) {
    /**
     * Core syllabus metadata.
     *
     * @param id              Unique syllabus ID (UUID).
     * @param sourceId        ID of the knowledge source this syllabus was extracted from.
     * @param courseCode      Course code (e.g. "CS106").
     * @param courseNameVi    Course name in Vietnamese.
     * @param courseNameEn    Course name in English.
     * @param credits         Number of academic credits.
     * @param theoryHours     Total theory/lecture hours.
     * @param practiceHours   Total practice/lab hours.
     * @param selfStudyHours  Expected self-study hours.
     * @param prerequisite    Prerequisite course code(s).
     * @param previousCourse  Recommended prior course.
     * @param department      Department offering the course.
     * @param description     Course description text.
     */
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

    /**
     * A learning objective mapped to one or more outcomes.
     *
     * @param id          Unique objective ID (UUID).
     * @param courseCode  Associated course code.
     * @param description Objective description text.
     * @param outcomeRefs JSON array of outcome codes this objective maps to (e.g. ["PLO1", "PLO2"]).
     */
    public record ObjectiveDto(
        UUID id,
        String courseCode,
        String description,
        com.fasterxml.jackson.databind.JsonNode outcomeRefs
    ) {}

    /**
     * A program learning outcome.
     *
     * @param id          Unique outcome ID (UUID).
     * @param courseCode  Associated course code.
     * @param outcomeCode Outcome code (e.g. "PLO1").
     * @param description Outcome description text.
     */
    public record OutcomeDto(
        UUID id,
        String courseCode,
        String outcomeCode,
        String description
    ) {}

    /**
     * A single teaching session (theory or practice) within the syllabus.
     *
     * @param id                 Unique session ID (UUID).
     * @param courseCode         Associated course code.
     * @param sourceId           ID of the source document.
     * @param sessionNo          Session number (e.g. "1", "2").
     * @param sessionType        Type: "THEORY" or "PRACTICE".
     * @param topic              Topic covered.
     * @param activities         Learning activities.
     * @param assessmentComponent Assessment component code (if any).
     */
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

    /**
     * An assessment component with grading weight.
     *
     * @param id            Unique assessment ID (UUID).
     * @param courseCode    Associated course code.
     * @param componentCode Assessment code (e.g. "BT", "GK", "CK").
     * @param description   Assessment description.
     * @param weightPercent Weight as percentage of total grade (e.g. 30).
     */
    public record AssessmentDto(
        UUID id,
        String courseCode,
        String componentCode,
        String description,
        Integer weightPercent
    ) {}
}
