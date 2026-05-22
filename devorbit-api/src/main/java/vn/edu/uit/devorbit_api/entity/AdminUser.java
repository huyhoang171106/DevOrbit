package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ADMIN USER = a person who can manage the DevOrbit system.
 *
 * Maps to the "admin_users" table.
 * Admin users log in through POST /api/admin/auth/login
 * and get a JWT token that grants access to /api/admin/** endpoints.
 *
 * Compare with StudentUser — students have student codes and emails;
 * admins only have a username.
 */
@Entity
@Table(name = "admin_users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The admin's login username (e.g., "admin", "superadmin") */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * The hashed (encrypted) password. Never stores the raw password.
     * Uses BCrypt (via SecurityConfig.passwordEncoder()).
     */
    @Column(nullable = false)
    private String passwordHash;

    /** Whether this admin account is active. Inactive admins cannot log in. */
    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
