package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.AdminUser;

import java.util.Optional;

/**
 * ADMIN USER REPOSITORY = data access for admin accounts.
 *
 * This is the SIMPLEST repository in the system — only one custom query.
 * Admin login flow:
 *   1. AdminAuthService.login() calls findByUsername()
 *   2. Checks password hash with BCrypt
 *   3. Generates JWT token
 */
@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    /**
     * Find an admin by their username (used during login).
     * Returns Optional — empty if username doesn't exist (prevents NullPointerException).
     *
     * @param username the login username
     * @return Optional containing AdminUser, or empty if not found
     */
    Optional<AdminUser> findByUsername(String username);
}
