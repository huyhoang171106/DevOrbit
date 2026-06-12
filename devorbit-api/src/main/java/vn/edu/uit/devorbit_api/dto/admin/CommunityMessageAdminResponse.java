package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CommunityMessageAdminResponse {
    private Long id;
    private String channelName;
    private String studentName;
    private String content;
    private LocalDateTime createdAt;
}
