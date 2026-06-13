package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminStudentResponse {
    private Long id;
    private String studentCode;
    private String fullName;
    private String email;
    private boolean active;
    private boolean emailVerified;
}
