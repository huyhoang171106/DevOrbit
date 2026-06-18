package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.AdminStudentResponse;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Admin controller for managing student accounts.
 * <p>
 * Endpoints:
 * <ul>
 *   <li>GET /api/admin/students - List or search students</li>
 *   <li>PUT /api/admin/students/{id}/toggle-active - Toggle student active status</li>
 * </ul>
 * <p>
 * Security: ADMIN role required.
 * Search supports partial match on studentCode, fullName, or email.
 * Toggle-active flips the boolean active flag and saves.
 */
@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentUserRepository studentRepo;

    /**
     * List all students or search by keyword.
     * <p>
     * If a non-blank search param is provided, matches students whose
     * studentCode, fullName, or email contain the search string.
     * Otherwise returns all students ordered by ID descending.
     *
     * @param search Optional keyword to search across studentCode, fullName, email
     * @return 200 OK with list of AdminStudentResponse
     * @apiNote GET /api/admin/students?search=keyword
     */
    @GetMapping
    public ResponseEntity<List<AdminStudentResponse>> listStudents(
            @RequestParam(required = false) String search) {
        List<StudentUser> students;
        if (search != null && !search.isBlank()) {
            students = studentRepo.findByStudentCodeContainingOrFullNameContainingOrEmailContaining(search, search, search);
        } else {
            students = studentRepo.findAllByOrderByIdDesc();
        }
        return ResponseEntity.ok(students.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    /**
     * Toggle the active status of a student account.
     * <p>
     * If currently active, becomes inactive (disabled), and vice versa.
     *
     * @param id Long ID of the student
     * @return 200 OK with updated AdminStudentResponse
     * @throws NotFoundException if student not found
     * @apiNote PUT /api/admin/students/{id}/toggle-active
     */
    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<AdminStudentResponse> toggleActive(@PathVariable Long id) {
        StudentUser student = studentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Student not found"));
        student.setActive(!student.isActive());
        return ResponseEntity.ok(toResponse(studentRepo.save(student)));
    }

    /**
     * Map a StudentUser entity to AdminStudentResponse DTO.
     *
     * @param s StudentUser entity
     * @return AdminStudentResponse with id, studentCode, fullName, email, active, emailVerified
     */
    private AdminStudentResponse toResponse(StudentUser s) {
        return AdminStudentResponse.builder()
            .id(s.getId())
            .studentCode(s.getStudentCode())
            .fullName(s.getFullName())
            .email(s.getEmail())
            .active(s.isActive())
            .emailVerified(s.isEmailVerified())
            .build();
    }
}
