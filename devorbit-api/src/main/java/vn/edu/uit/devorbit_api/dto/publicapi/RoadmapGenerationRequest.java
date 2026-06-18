package vn.edu.uit.devorbit_api.dto.publicapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ROADMAP GENERATION REQUEST = params for the AI graduation roadmap.
 *
 * POST /api/ai/roadmap-recommendations
 *
 * learningGoals: what the student wants to study / achieve
 * careerPath: the intended career direction
 *
 * The AI uses both fields to build a personalized 4-year study plan
 * covering mandatory courses, electives, and graduation tracks.
 */
public record RoadmapGenerationRequest(
    @NotBlank @Size(max = 2000) String learningGoals,
    @NotBlank @Size(max = 200) String careerPath
) {}
