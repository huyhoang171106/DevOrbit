package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.uit.devorbit_api.dto.student.StudentAuthResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentLoginRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentProfileResponse;
import vn.edu.uit.devorbit_api.service.StudentAuthService;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentAuthController {
    private final StudentAuthService studentAuthService;

    @PostMapping("/login")
    public StudentAuthResponse login(@RequestBody @Valid StudentLoginRequest request) {
        return studentAuthService.login(request);
    }

    @GetMapping("/me")
    public StudentProfileResponse me(@AuthenticationPrincipal String studentCode) {
        return studentAuthService.me(studentCode);
    }
}