package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * STUDENT USER = a student who uses the DevOrbit platform.
 *
 * Maps to the "student_users" table.
 * Students register with their university student code and email.
 * They can create accounts, bookmark resources, write notes,
 * and generate personalized learning roadmaps.
 *
 * Compare with AdminUser — admins manage the system; students learn.
 */
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

    /** University student ID (e.g., "21520100") */
    @Column(nullable = false, unique = true)
    private String studentCode;

    /** Student's full name */
    @Column(nullable = false)
    private String fullName;

    /** University email address */
    @Column(nullable = false, unique = true)
    private String email;

    /** Hashed (encrypted) password using BCrypt */
    @Column(nullable = false)
    private String passwordHash;

    /** Is this account active? Inactive accounts can't log in. */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    /** Has this student verified their email via OTP? */
    @Column(name = "email_verified", nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

<<<<<<< HEAD
    /** URL to the student's avatar image */
    @Column
    private String avatar;
}
=======
    /** Incremented when all existing student JWTs should become invalid. */
    @Column(name = "token_version", nullable = false)
    @Builder.Default
    private int tokenVersion = 0;

    /** URL to the student's avatar image */
    @Column
    private String avatar;
}
>>>>>>> master
