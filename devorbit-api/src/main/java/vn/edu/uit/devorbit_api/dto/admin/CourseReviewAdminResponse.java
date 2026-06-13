package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CourseReviewAdminResponse {
    private Long id;
    private String studentName;
    private String courseName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
