package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.entity.CourseSyllabus;
import vn.edu.uit.devorbit_api.entity.CourseAssessment;
import vn.edu.uit.devorbit_api.entity.CourseObjective;
import vn.edu.uit.devorbit_api.entity.CourseOutcome;
import vn.edu.uit.devorbit_api.entity.CourseSession;
import vn.edu.uit.devorbit_api.entity.CourseTool;
import vn.edu.uit.devorbit_api.repository.CourseSyllabusRepository;
import vn.edu.uit.devorbit_api.repository.CourseAssessmentRepository;
import vn.edu.uit.devorbit_api.repository.CourseObjectiveRepository;
import vn.edu.uit.devorbit_api.repository.CourseOutcomeRepository;
import vn.edu.uit.devorbit_api.repository.CourseSessionRepository;
import vn.edu.uit.devorbit_api.repository.CourseToolRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseFactQueryServiceTest {

    @Mock private CourseSyllabusRepository syllabusRepository;
    @Mock private CourseAssessmentRepository assessmentRepository;
    @Mock private CourseObjectiveRepository objectiveRepository;
    @Mock private CourseOutcomeRepository outcomeRepository;
    @Mock private CourseSessionRepository sessionRepository;
    @Mock private CourseToolRepository toolRepository;

    private CourseFactQueryService service;

    @BeforeEach
    void setUp() {
        service = new CourseFactQueryService(
            syllabusRepository, assessmentRepository, objectiveRepository,
            outcomeRepository, sessionRepository, toolRepository);
    }

    @Test
    void getCredits_existingCourse_returnsCredits() {
        CourseSyllabus syllabus = new CourseSyllabus();
        syllabus.setCredits(4);
        when(syllabusRepository.findByCourseCode("IT003"))
            .thenReturn(Optional.of(syllabus));

        var result = service.getFact("IT003", "credits");

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("4 tín chỉ");
    }

    @Test
    void getPrerequisite_existingCourse_returnsPrereq() {
        CourseSyllabus syllabus = new CourseSyllabus();
        syllabus.setPrerequisite("Nhập môn lập trình");
        when(syllabusRepository.findByCourseCode("IT003"))
            .thenReturn(Optional.of(syllabus));

        var result = service.getFact("IT003", "prerequisite");

        assertThat(result).isPresent();
        assertThat(result.get()).contains("Nhập môn lập trình");
    }

    @Test
    void getAssessment_weight_returnsPercent() {
        when(syllabusRepository.findByCourseCode("IT003"))
            .thenReturn(Optional.of(new CourseSyllabus()));
        CourseAssessment a = new CourseAssessment();
        a.setComponentCode("A2");
        a.setWeightPercent(20);
        when(assessmentRepository.findByCourseCode("IT003"))
            .thenReturn(List.of(a));

        var result = service.getFact("IT003", "assessment:A2");

        assertThat(result).isPresent();
        assertThat(result.get()).contains("20%");
    }

    @Test
    void getPracticeHours_existingCourse_returnsHours() {
        CourseSyllabus syllabus = new CourseSyllabus();
        syllabus.setPracticeHours(30);
        when(syllabusRepository.findByCourseCode("IT003"))
            .thenReturn(Optional.of(syllabus));

        var result = service.getFact("IT003", "practiceHours");

        assertThat(result).isPresent();
        assertThat(result.get()).contains("30");
    }

    @Test
    void getFact_unknownField_returnsEmpty() {
        CourseSyllabus syllabus = new CourseSyllabus();
        when(syllabusRepository.findByCourseCode("IT003"))
            .thenReturn(Optional.of(syllabus));

        var result = service.getFact("IT003", "unknownField");

        assertThat(result).isEmpty();
    }

    @Test
    void getFact_nonexistentCourse_returnsEmpty() {
        when(syllabusRepository.findByCourseCode("XX999"))
            .thenReturn(Optional.empty());

        var result = service.getFact("XX999", "credits");

        assertThat(result).isEmpty();
    }
}
