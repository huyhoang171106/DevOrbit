package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.entity.CourseAssessment;
import vn.edu.uit.devorbit_api.entity.CourseSyllabus;
import vn.edu.uit.devorbit_api.repository.CourseAssessmentRepository;
import vn.edu.uit.devorbit_api.repository.CourseObjectiveRepository;
import vn.edu.uit.devorbit_api.repository.CourseOutcomeRepository;
import vn.edu.uit.devorbit_api.repository.CourseSessionRepository;
import vn.edu.uit.devorbit_api.repository.CourseSyllabusRepository;
import vn.edu.uit.devorbit_api.repository.CourseToolRepository;

import java.util.List;
import java.util.Optional;

/**
 * Queries structured course facts directly from PostgreSQL.
 * Used for FACT_QUERY intent — no LLM needed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseFactQueryService {

    private final CourseSyllabusRepository syllabusRepository;
    private final CourseAssessmentRepository assessmentRepository;
    private final CourseObjectiveRepository objectiveRepository;
    private final CourseOutcomeRepository outcomeRepository;
    private final CourseSessionRepository sessionRepository;
    private final CourseToolRepository toolRepository;

    /**
     * Get a specific fact about a course.
     * @param courseCode e.g. "IT003"
     * @param factType e.g. "credits", "prerequisite", "assessment:A2", "practiceHours"
     * @return formatted answer string, or empty if not found
     */
    public Optional<String> getFact(String courseCode, String factType) {
        Optional<CourseSyllabus> syllabusOpt = syllabusRepository.findByCourseCode(courseCode);
        if (syllabusOpt.isEmpty()) {
            return Optional.empty();
        }

        CourseSyllabus syllabus = syllabusOpt.get();

        return switch (factType) {
            case "credits" -> syllabus.getCredits() != null
                ? Optional.of(syllabus.getCredits() + " tín chỉ")
                : Optional.empty();
            case "prerequisite" -> syllabus.getPrerequisite() != null && !syllabus.getPrerequisite().isBlank()
                ? Optional.of("Tiên quyết: " + syllabus.getPrerequisite())
                : Optional.empty();
            case "previousCourse" -> syllabus.getPreviousCourse() != null && !syllabus.getPreviousCourse().isBlank()
                ? Optional.of("Môn liên quan: " + syllabus.getPreviousCourse())
                : Optional.empty();
            case "theoryHours" -> syllabus.getTheoryHours() != null
                ? Optional.of("Số giờ lý thuyết: " + syllabus.getTheoryHours() + " giờ")
                : Optional.empty();
            case "practiceHours" -> syllabus.getPracticeHours() != null
                ? Optional.of("Số giờ thực hành: " + syllabus.getPracticeHours() + " giờ")
                : Optional.empty();
            case "selfStudyHours" -> syllabus.getSelfStudyHours() != null
                ? Optional.of("Số giờ tự học: " + syllabus.getSelfStudyHours() + " giờ")
                : Optional.empty();
            case "description" -> syllabus.getDescription() != null && !syllabus.getDescription().isBlank()
                ? Optional.of(syllabus.getDescription())
                : Optional.empty();
            case "courseName" -> {
                String name = syllabus.getCourseNameVi() != null ? syllabus.getCourseNameVi() : syllabus.getCourseNameEn();
                yield name != null ? Optional.of(name) : Optional.empty();
            }
            default -> handleCompoundFact(courseCode, factType, syllabus);
        };
    }

    private Optional<String> handleCompoundFact(String courseCode, String factType, CourseSyllabus syllabus) {
        if (factType.startsWith("assessment:")) {
            String componentCode = factType.substring("assessment:".length()).trim().toUpperCase();
            List<CourseAssessment> assessments = assessmentRepository.findByCourseCode(courseCode);
            return assessments.stream()
                .filter(a -> componentCode.equals(a.getComponentCode()))
                .findFirst()
                .map(a -> a.getComponentCode() + ": " + a.getWeightPercent() + "%"
                    + (a.getDescription() != null ? " (" + a.getDescription() + ")" : ""));
        }

        if ("assessments".equals(factType)) {
            List<CourseAssessment> assessments = assessmentRepository.findByCourseCode(courseCode);
            if (assessments.isEmpty()) return Optional.empty();
            StringBuilder sb = new StringBuilder("Đánh giá:\n");
            for (CourseAssessment a : assessments) {
                sb.append("- ").append(a.getComponentCode()).append(": ")
                  .append(a.getWeightPercent()).append("%");
                if (a.getDescription() != null) {
                    sb.append(" — ").append(a.getDescription());
                }
                sb.append("\n");
            }
            return Optional.of(sb.toString().trim());
        }

        if ("objectives".equals(factType)) {
            var objectives = objectiveRepository.findByCourseCode(courseCode);
            if (objectives.isEmpty()) return Optional.empty();
            StringBuilder sb = new StringBuilder("Mục tiêu học tập:\n");
            objectives.forEach(o -> sb.append("- ").append(o.getDescription()).append("\n"));
            return Optional.of(sb.toString().trim());
        }

        if ("outcomes".equals(factType)) {
            var outcomes = outcomeRepository.findByCourseCode(courseCode);
            if (outcomes.isEmpty()) return Optional.empty();
            StringBuilder sb = new StringBuilder("Kết quả học tập:\n");
            outcomes.forEach(o -> sb.append("- ").append(o.getOutcomeCode()).append(": ").append(o.getDescription()).append("\n"));
            return Optional.of(sb.toString().trim());
        }

        if ("sessions".equals(factType)) {
            var sessions = sessionRepository.findByCourseCode(courseCode);
            if (sessions.isEmpty()) return Optional.empty();
            StringBuilder sb = new StringBuilder("Chương trình học:\n");
            sessions.forEach(s -> sb.append("- Tuần ").append(s.getSessionNo())
                .append(": ").append(s.getTopic()).append("\n"));
            return Optional.of(sb.toString().trim());
        }

        return Optional.empty();
    }
}
