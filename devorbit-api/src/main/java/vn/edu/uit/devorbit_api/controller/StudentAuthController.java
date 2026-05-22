package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.student.StudentAuthResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentLoginRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentProfileResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentRegisterRequest;
import vn.edu.uit.devorbit_api.service.StudentAuthService;

/**
 * STUDENT AUTH CONTROLLER = register, login, and manage student accounts.
 *
 * Endpoints:
 *   POST /api/student/register    — creates a new student account
 *   POST /api/student/login       — logs in with student code + password
 *   GET  /api/student/me          — returns the logged-in student's profile
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentAuthController {

    private final StudentAuthService studentAuthService;

    /** Log in with student code + password. Returns JWT token. */
    @PostMapping("/login")
    public StudentAuthResponse login(@RequestBody @Valid StudentLoginRequest request) {
        return studentAuthService.login(request);
    }

    /** Register a new student account. Returns JWT token. */
    @PostMapping("/register")
    public StudentAuthResponse register(@RequestBody @Valid StudentRegisterRequest request) {
        return studentAuthService.register(request);
    }

    /**
     * Get the currently logged-in student's profile.
     * Uses @AuthenticationPrincipal to extract the student code from the JWT.
     */
    @GetMapping("/me")
    public StudentProfileResponse me(@AuthenticationPrincipal String studentCode) {
        return studentAuthService.me(studentCode);
    }
}
