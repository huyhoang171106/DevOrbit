package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String studentCode;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}