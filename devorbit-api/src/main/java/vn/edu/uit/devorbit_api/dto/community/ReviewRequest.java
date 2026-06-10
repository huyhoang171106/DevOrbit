package vn.edu.uit.devorbit_api.dto.community;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
        @NotNull
        @Min(1)
        @Max(5)
        Integer rating,

        @Size(max = 5000)
        String comment
) {
}
