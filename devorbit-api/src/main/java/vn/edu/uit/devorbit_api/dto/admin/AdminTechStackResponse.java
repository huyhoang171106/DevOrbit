package vn.edu.uit.devorbit_api.dto.admin;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AdminTechStackResponse {
    private Long id;
    private String name;
}
