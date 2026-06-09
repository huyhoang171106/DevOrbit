package vn.edu.uit.devorbit_api.service.knowledge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import vn.edu.uit.devorbit_api.dto.knowledge.ExtractedSyllabusFacts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validates extracted syllabus facts.
 * Returns warnings for minor issues, throws for critical failures.
 */
@Slf4j
@Component
public class SyllabusValidator {

    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile("^[A-Z]{2,4}[0-9]{3}$");

    /**
     * Validation result.
     */
    public record ValidationResult(
        List<String> warnings,
        boolean valid
    ) {
        public static ValidationResult success(List<String> warnings) {
            return new ValidationResult(warnings, true);
        }

        public static ValidationResult failure(String error) {
            return new ValidationResult(List.of(error), false);
        }
    }

    /**
     * Validate extracted syllabus facts.
     * Returns warnings for minor issues.
     * Throws IllegalArgumentException for critical failures.
     */
    public ValidationResult validate(ExtractedSyllabusFacts facts) {
        List<String> warnings = new ArrayList<>();

        // Hard fail: missing/invalid courseCode
        if (facts.courseCode() == null || facts.courseCode().isBlank()) {
            return ValidationResult.failure("courseCode is required");
        }
        if (!COURSE_CODE_PATTERN.matcher(facts.courseCode()).matches()) {
            return ValidationResult.failure("courseCode must match pattern [A-Z]{2,4}[0-9]{3}, got: " + facts.courseCode());
        }

        // Hard fail: empty course name
        if ((facts.courseNameVi() == null || facts.courseNameVi().isBlank()) &&
            (facts.courseNameEn() == null || facts.courseNameEn().isBlank())) {
            return ValidationResult.failure("At least one of courseNameVi or courseNameEn is required");
        }

        // Hard fail: credits must be present and in range
        if (facts.credits() == null) {
            return ValidationResult.failure("credits is required");
        }
        if (facts.credits() < 1 || facts.credits() > 10) {
            return ValidationResult.failure("credits must be between 1 and 10, got: " + facts.credits());
        }

        if (facts.theoryHours() != null && facts.theoryHours() < 0) {
            warnings.add("theoryHours should be >= 0, got: " + facts.theoryHours());
        }

        if (facts.practiceHours() != null && facts.practiceHours() < 0) {
            warnings.add("practiceHours should be >= 0, got: " + facts.practiceHours());
        }

        if (facts.selfStudyHours() != null && facts.selfStudyHours() < 0) {
            warnings.add("selfStudyHours should be >= 0, got: " + facts.selfStudyHours());
        }

        // Check assessment weights sum to 100
        if (facts.assessments() != null && !facts.assessments().isEmpty()) {
            int totalWeight = facts.assessments().stream()
                    .filter(a -> a.weightPercent() != null)
                    .mapToInt(ExtractedSyllabusFacts.AssessmentDto::weightPercent)
                    .sum();
            if (totalWeight != 100) {
                warnings.add("Assessment weights sum to " + totalWeight + " instead of 100. " +
                    "Note: A4 may appear as one component (50%) or split as A4a(40%)+A4b(10%)");
            }
        }

        if (!warnings.isEmpty()) {
            log.warn("Validation warnings for {}: {}", facts.courseCode(), warnings);
        }

        return ValidationResult.success(warnings);
    }
}
