package vn.edu.uit.devorbit_api.dto.publicapi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RoadmapGenerationRequest(
    @NotBlank
    @Size(max = 2000)
    String learningGoals,

    @NotBlank
    @Size(max = 200)
    String careerPath
) {}
