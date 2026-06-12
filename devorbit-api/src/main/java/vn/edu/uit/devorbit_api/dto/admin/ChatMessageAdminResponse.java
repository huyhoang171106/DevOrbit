package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatMessageAdminResponse {
    private Long id;
    private String sender;
    private String content;
    private LocalDateTime createdAt;
}
