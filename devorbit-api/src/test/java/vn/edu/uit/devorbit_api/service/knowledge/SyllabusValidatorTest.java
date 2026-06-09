package vn.edu.uit.devorbit_api.service.knowledge;

import org.junit.jupiter.api.Test;
import vn.edu.uit.devorbit_api.dto.knowledge.ExtractedSyllabusFacts;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SyllabusValidatorTest {

    private final SyllabusValidator validator = new SyllabusValidator();

    private ExtractedSyllabusFacts validFacts() {
        return new ExtractedSyllabusFacts(
                "IT001", "Course A", "Course A EN",
                3, 30, 15, 15, null, null, "CS", "Description",
                List.of(new ExtractedSyllabusFacts.ObjectiveDto("Objective 1", List.of("CO1"))),
                List.of(new ExtractedSyllabusFacts.OutcomeDto("CO1", "Outcome 1")),
                List.of(), List.of(),
                List.of(new ExtractedSyllabusFacts.AssessmentDto("A1", "Midterm", 30),
                         new ExtractedSyllabusFacts.AssessmentDto("A2", "Final", 70)),
                List.of(), List.of()
        );
    }

    @Test
    void hardFail_onNullCourseCode() {
        var facts = new ExtractedSyllabusFacts(
                null, "Name", null, 3, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );
        var result = validator.validate(facts);
        assertThat(result.valid()).isFalse();
        assertThat(result.warnings()).anyMatch(w -> w.contains("courseCode is required"));
    }

    @Test
    void hardFail_onInvalidCourseCode() {
        var facts = new ExtractedSyllabusFacts(
                "INVALID", "Name", null, 3, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );
        var result = validator.validate(facts);
        assertThat(result.valid()).isFalse();
        assertThat(result.warnings()).anyMatch(w -> w.contains("must match pattern"));
    }

    @Test
    void hardFail_whenBothNamesNull() {
        var facts = new ExtractedSyllabusFacts(
                "IT001", null, null, 3, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );
        var result = validator.validate(facts);
        assertThat(result.valid()).isFalse();
        assertThat(result.warnings()).anyMatch(w -> w.contains("courseNameVi") || w.contains("courseNameEn"));
    }

    @Test
    void hardFail_onCreditsNull() {
        var facts = new ExtractedSyllabusFacts(
                "IT001", "Name", null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );
        var result = validator.validate(facts);
        assertThat(result.valid()).isFalse();
        assertThat(result.warnings()).anyMatch(w -> w.contains("credits is required"));
    }

    @Test
    void hardFail_onCreditsOutOfRange() {
        var facts = new ExtractedSyllabusFacts(
                "IT001", "Name", null, 15, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );
        var result = validator.validate(facts);
        assertThat(result.valid()).isFalse();
        assertThat(result.warnings()).anyMatch(w -> w.contains("credits must be between 1 and 10"));
    }

    @Test
    void hardFail_onCreditsZero() {
        var facts = new ExtractedSyllabusFacts(
                "IT001", "Name", null, 0, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );
        var result = validator.validate(facts);
        assertThat(result.valid()).isFalse();
        assertThat(result.warnings()).anyMatch(w -> w.contains("credits must be between 1 and 10"));
    }

    @Test
    void warning_onNegativeHours() {
        var facts = new ExtractedSyllabusFacts(
                "IT001", "Name", null, 3, -5, null, null, null, null, null, null,
                null, null, null, null, null, null, null
        );
        var result = validator.validate(facts);
        assertThat(result.valid()).isTrue();
        assertThat(result.warnings()).anyMatch(w -> w.contains("theoryHours should be >= 0"));
    }

    @Test
    void warning_onAssessmentWeightsNot100() {
        var facts = new ExtractedSyllabusFacts(
                "IT001", "Name", null, 3, null, null, null, null, null, null, null,
                null, null, null, null,
                List.of(new ExtractedSyllabusFacts.AssessmentDto("A1", "Midterm", 40),
                         new ExtractedSyllabusFacts.AssessmentDto("A2", "Final", 40)),
                null, null
        );
        var result = validator.validate(facts);
        assertThat(result.valid()).isTrue();
        assertThat(result.warnings()).anyMatch(w -> w.contains("sum to 80") && w.contains("A4"));
    }

    @Test
    void validFacts_passWithNoWarnings() {
        var result = validator.validate(validFacts());
        assertThat(result.valid()).isTrue();
        assertThat(result.warnings()).isEmpty();
    }
}
