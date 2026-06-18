package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.StudentUser;

import java.util.List;
import java.util.Optional;

/**
 * STUDENT USER REPOSITORY = data access for student accounts.
 *
 * Used by StudentAuthService for login, registration, profile management.
 * Two unique identifiers: studentCode (university ID) and email.
 *
 * Key flows:
 *   Login             → findByStudentCode() + verify password
 *   Register          → findByStudentCode() + findByEmail() (check duplicates)
 *   Forgot Password   → findByEmail() (send reset OTP)
 *   Admin search      → findByStudentCodeContainingOr... (search by code/name/email)
 */
@Repository
public interface StudentUserRepository extends JpaRepository<StudentUser, Long> {

    /** Find by university student code (e.g., "21520100"). Used in login. */
    Optional<StudentUser> findByStudentCode(String studentCode);

    /** Find by email address. Used in OTP verification and forgot-password flows. */
    Optional<StudentUser> findByEmail(String email);

    /** Last 10 registered students (for admin dashboard widget). */
    List<StudentUser> findTop10ByOrderByIdDesc();

    /** All students sorted by most recent first (for admin management page). */
    List<StudentUser> findAllByOrderByIdDesc();

    /**
     * Search students by partial match on studentCode, fullName, OR email.
     * Used by admin search bar.
     * The method name generates SQL: WHERE student_code LIKE %?% OR full_name LIKE %?% OR email LIKE %?%
     *
     * @param studentCode search term (applied to student_code field)
     * @param fullName    same search term (applied to full_name field)
     * @param email       same search term (applied to email field)
     */
    List<StudentUser> findByStudentCodeContainingOrFullNameContainingOrEmailContaining(
        String studentCode, String fullName, String email);
}