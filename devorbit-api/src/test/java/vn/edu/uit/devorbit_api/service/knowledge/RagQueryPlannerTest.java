package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.repository.CourseRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RagQueryPlannerTest {

    @Mock
    private CourseRepository courseRepository;

    private RagQueryPlanner planner;

    @BeforeEach
    void setUp() {
        planner = new RagQueryPlanner(courseRepository);
    }

    @Test
    void plan_expandsCourseCodeWithCourseNameAndIntentTerms() {
        String query = "SE104 kinh nghiem hoc tap";
        Course course = Course.builder()
                .maMH("SE104")
                .tenMH("Nhập môn công nghệ phần mềm")
                .loaiMonHoc("CO_SO_NGANH")
                .build();
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(course));

        RagQueryPlan plan = planner.plan(query, null);

        assertThat(plan.originalQuery()).isEqualTo(query);
        assertThat(plan.detectedCourseCodes()).contains("SE104");
        assertThat(plan.primaryQuery())
            .contains("SE104")
            .contains("Nhập môn công nghệ phần mềm")
            .contains("phuong phap hoc tap");
        assertThat(plan.textQuery())
            .contains("nhap mon cong nghe phan mem");
        assertThat(plan.expandedQueries()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void plan_detectsMultipleCourseCodes() {
        String query = "so sánh SE104 và IT003";
        Course se104 = Course.builder().maMH("SE104").tenMH("SE104").build();
        Course it003 = Course.builder().maMH("IT003").tenMH("IT003").build();
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(se104));
        when(courseRepository.findByMaMH("IT003")).thenReturn(Optional.of(it003));

        RagQueryPlan plan = planner.plan(query, null);

        assertThat(plan.detectedCourseCodes()).contains("SE104", "IT003");
    }

    @Test
    void plan_keepsWorkingWhenCourseLookupFails() {
        String query = "SE104 hoc phan nay the nao";
        when(courseRepository.findByMaMH("SE104")).thenThrow(new RuntimeException("DB error"));

        RagQueryPlan plan = planner.plan(query, null);

        assertThat(plan.originalQuery()).isEqualTo(query);
        assertThat(plan.detectedCourseCodes()).contains("SE104");
        assertThat(plan.expandedQueries()).isNotEmpty();
    }

    @Test
    void plan_includesScopedCourseCode() {
        String query = "hoc the nao";
        Course course = Course.builder().maMH("IT001").tenMH("IT001").build();
        when(courseRepository.findByMaMH("IT001")).thenReturn(Optional.of(course));

        RagQueryPlan plan = planner.plan(query, "IT001");

        assertThat(plan.detectedCourseCodes()).contains("IT001");
    }

    @Test
    void plan_returnsAllBlankOnEmptyQuery() {
        RagQueryPlan plan = planner.plan("", null);

        assertThat(plan.originalQuery()).isEqualTo("");
        assertThat(plan.primaryQuery()).isEqualTo("");
        assertThat(plan.textQuery()).isEqualTo("");
        assertThat(plan.expandedQueries()).isEmpty();
        assertThat(plan.detectedCourseCodes()).isEmpty();
    }

    @Test
    void plan_deduplicatesExpandedQueries() {
        // Query that already matches the normalized text should not produce duplicates
        String query = "SE104 hoc phan nay";
        Course course = Course.builder().maMH("SE104").tenMH("SE104").build();
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(course));

        RagQueryPlan plan = planner.plan(query, null);

        // Should have at least raw query + primaryQuery
        assertThat(plan.expandedQueries()).doesNotHaveDuplicates();
        assertThat(plan.expandedQueries().get(0)).isEqualTo(query);
    }

    @Test
    void plan_expandsIntentTerms() {
        String query = "de cuong SE104";
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(Course.builder().maMH("SE104").tenMH("SE104").build()));

        RagQueryPlan plan = planner.plan(query, null);

        assertThat(plan.primaryQuery()).contains("de cuong mon hoc giao trinh");
        assertThat(plan.textQuery()).contains("de cuong");
    }

    @Test
    void plan_expandsProjectIntent() {
        String query = "SE104 co github nao khong";
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(Course.builder().maMH("SE104").tenMH("SE104").build()));

        RagQueryPlan plan = planner.plan(query, null);

        assertThat(plan.primaryQuery()).contains("do an thuc hanh repository github");
    }

    @Test
    void plan_textQueryStripsPunctuation() {
        String query = "kinh nghiệm học SE104?";
        when(courseRepository.findByMaMH("SE104")).thenReturn(Optional.of(Course.builder().maMH("SE104").tenMH("SE104").build()));

        RagQueryPlan plan = planner.plan(query, null);

        assertThat(plan.textQuery()).doesNotContain("?");
    }
}
