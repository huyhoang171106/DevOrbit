package vn.edu.uit.devorbit_api.dto.knowledge;

import java.util.List;

/**
 * DTO holding extracted syllabus facts after parsing a syllabus document.
 * This is the raw extraction result before persistence.
 * Inner records represent structured sub-parts of the syllabus.
 *
 * @param courseCode      Course identifier code (e.g. "CS106").
 * @param courseNameVi    Course name in Vietnamese.
 * @param courseNameEn    Course name in English (if available).
 * @param credits         Number of academic credits.
 * @param theoryHours     Total theory/lecture hours.
 * @param practiceHours   Total practice/lab hours.
 * @param selfStudyHours  Expected self-study hours.
 * @param prerequisite    Prerequisite course code(s).
 * @param previousCourse  Recommended prior course.
 * @param department      Department offering the course.
 * @param description     Course description text.
 * @param objectives      List of learning objectives, each with outcome references.
 * @param outcomes        List of learning outcomes (PLOs).
 * @param theorySessions  Scheduled theory sessions.
 * @param practiceSessions Scheduled practice/lab sessions.
 * @param assessments     List of assessment components with weight percentages.
 * @param references      List of textbook/reference titles.
 * @param tools           List of tools/software used in the course.
 */
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
    /**
     * A learning objective with references to one or more outcomes.
     *
     * @param description Human-readable objective statement.
     * @param outcomeRefs Codes of the outcomes this objective maps to (e.g. ["PLO1", "PLO2"]).
     */
    public record ObjectiveDto(
        String description,
        List<String> outcomeRefs
    ) {}

    /**
     * A program learning outcome.
     *
     * @param code        Outcome code (e.g. "PLO1").
     * @param description Detailed outcome description.
     */
    public record OutcomeDto(
        String code,
        String description
    ) {}

    /**
     * A single teaching session (theory or practice).
     *
     * @param sessionNo          Session number (e.g. "1", "2").
     * @param topic              Topic covered in this session.
     * @param activities         Teaching/learning activities.
     * @param assessmentComponent Associated assessment component code (if any).
     */
    public record SessionDto(
        String sessionNo,
        String topic,
        String activities,
        String assessmentComponent
    ) {}

    /**
     * An assessment component with its weight.
     *
     * @param componentCode  Assessment code (e.g. "BT", "GK", "CK").
     * @param description    Description of the assessment.
     * @param weightPercent  Weight as percentage of total grade (e.g. 30).
     */
    public record AssessmentDto(
        String componentCode,
        String description,
        Integer weightPercent
    ) {}
}
