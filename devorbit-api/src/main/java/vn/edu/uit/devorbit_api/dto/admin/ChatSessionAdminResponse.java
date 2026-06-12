package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatSessionAdminResponse {
    private UUID id;
    private String studentName;
    private String title;
    private long messageCount;
    private LocalDateTime createdAt;
}
