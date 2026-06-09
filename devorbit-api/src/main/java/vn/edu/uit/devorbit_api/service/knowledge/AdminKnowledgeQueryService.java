package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.knowledge.KnowledgeSourceResponse;
import vn.edu.uit.devorbit_api.dto.knowledge.SyllabusDetailsResponse;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Read-only queries for knowledge admin inspection.
 */
@Service
@RequiredArgsConstructor
public class AdminKnowledgeQueryService {

    private final KnowledgeSourceService knowledgeSourceService;
    private final CourseKnowledgeIndexer courseKnowledgeIndexer;
    private final CourseSyllabusRepository courseSyllabusRepository;
    private final CourseObjectiveRepository courseObjectiveRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;
    private final CourseSessionRepository courseSessionRepository;
    private final CourseAssessmentRepository courseAssessmentRepository;
    private final CourseReferenceRepository courseReferenceRepository;
    private final CourseToolRepository courseToolRepository;

    public List<KnowledgeSourceResponse> listSources() {
        return knowledgeSourceService.findAll().stream()
                .map(this::toSourceResponse)
                .collect(Collectors.toList());
    }

    public SyllabusDetailsResponse getCourseDetails(String courseCode) {
        CourseSyllabus syllabus = courseSyllabusRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseCode));

        List<CourseObjective> objectives = courseObjectiveRepository.findByCourseCode(courseCode);
        List<CourseOutcome> outcomes = courseOutcomeRepository.findByCourseCode(courseCode);
        List<CourseSession> sessions = courseSessionRepository.findByCourseCode(courseCode);
        List<CourseAssessment> assessments = courseAssessmentRepository.findByCourseCode(courseCode);
        List<CourseReference> references = courseReferenceRepository.findByCourseCode(courseCode);
        List<CourseTool> tools = courseToolRepository.findByCourseCode(courseCode);

        return new SyllabusDetailsResponse(
                toSyllabusDto(syllabus),
                objectives.stream().map(this::toObjectiveDto).collect(Collectors.toList()),
                outcomes.stream().map(this::toOutcomeDto).collect(Collectors.toList()),
                sessions.stream().map(this::toSessionDto).collect(Collectors.toList()),
                assessments.stream().map(this::toAssessmentDto).collect(Collectors.toList()),
                references.stream().map(CourseReference::getReferenceText).collect(Collectors.toList()),
                tools.stream().map(CourseTool::getToolName).collect(Collectors.toList())
        );
    }

    public List<KnowledgeChunk> getCourseChunks(String courseCode) {
        return courseKnowledgeIndexer.getChunks(courseCode);
    }

    // ===== DTO MAPPING =====

    private KnowledgeSourceResponse toSourceResponse(KnowledgeSource source) {
        return new KnowledgeSourceResponse(
                source.getId(),
                source.getSourceType(),
                source.getFileName(),
                source.getStatus(),
                source.getContentHash(),
                source.getErrorMessage(),
                source.getUpdatedAt()
        );
    }

    private SyllabusDetailsResponse.SyllabusDto toSyllabusDto(CourseSyllabus syllabus) {
        return new SyllabusDetailsResponse.SyllabusDto(
                syllabus.getId(),
                syllabus.getSource().getId(),
                syllabus.getCourseCode(),
                syllabus.getCourseNameVi(),
                syllabus.getCourseNameEn(),
                syllabus.getCredits(),
                syllabus.getTheoryHours(),
                syllabus.getPracticeHours(),
                syllabus.getSelfStudyHours(),
                syllabus.getPrerequisite(),
                syllabus.getPreviousCourse(),
                syllabus.getDepartment(),
                syllabus.getDescription()
        );
    }

    private SyllabusDetailsResponse.ObjectiveDto toObjectiveDto(CourseObjective obj) {
        return new SyllabusDetailsResponse.ObjectiveDto(
                obj.getId(),
                obj.getCourseCode(),
                obj.getDescription(),
                obj.getOutcomeRefs()
        );
    }

    private SyllabusDetailsResponse.OutcomeDto toOutcomeDto(CourseOutcome out) {
        return new SyllabusDetailsResponse.OutcomeDto(
                out.getId(),
                out.getCourseCode(),
                out.getOutcomeCode(),
                out.getDescription()
        );
    }

    private SyllabusDetailsResponse.SessionDto toSessionDto(CourseSession session) {
        return new SyllabusDetailsResponse.SessionDto(
                session.getId(),
                session.getCourseCode(),
                session.getSource().getId(),
                session.getSessionNo(),
                session.getSessionType(),
                session.getTopic(),
                session.getActivities(),
                session.getAssessmentComponent()
        );
    }

    private SyllabusDetailsResponse.AssessmentDto toAssessmentDto(CourseAssessment assessment) {
        return new SyllabusDetailsResponse.AssessmentDto(
                assessment.getId(),
                assessment.getCourseCode(),
                assessment.getComponentCode(),
                assessment.getDescription(),
                assessment.getWeightPercent()
        );
    }
}
