package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class RepoReviewAdminResponse {
    private Long id;
    private String studentName;
    private String repoName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
