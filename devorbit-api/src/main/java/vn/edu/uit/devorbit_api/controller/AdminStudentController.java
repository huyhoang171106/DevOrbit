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

@RestController
@RequestMapping("/api/admin/students")
@RequiredArgsConstructor
public class AdminStudentController {

    private final StudentUserRepository studentRepo;

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

    @PutMapping("/{id}/toggle-active")
    public ResponseEntity<AdminStudentResponse> toggleActive(@PathVariable Long id) {
        StudentUser student = studentRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Student not found"));
        student.setActive(!student.isActive());
        return ResponseEntity.ok(toResponse(studentRepo.save(student)));
    }

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
