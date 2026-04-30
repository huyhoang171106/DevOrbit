package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.student.StudentAuthResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentLoginRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentProfileResponse;
import vn.edu.uit.devorbit_api.dto.student.StudentRegisterRequest;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.UnauthorizedException;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

@Service
@RequiredArgsConstructor
public class StudentAuthService {
    private final StudentUserRepository studentUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public StudentAuthResponse login(StudentLoginRequest request) {
        StudentUser student = studentUserRepository.findByStudentCode(request.studentCode())
                .orElseThrow(() -> new UnauthorizedException("Invalid student code or password"));

        if (!student.isActive() || !passwordEncoder.matches(request.password(), student.getPasswordHash())) {
            throw new UnauthorizedException("Invalid student code or password");
        }

        String token = jwtService.generateToken(student.getStudentCode(), "STUDENT");
        return new StudentAuthResponse(token, student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail());
    }

    public StudentAuthResponse register(StudentRegisterRequest request) {
        if (studentUserRepository.findByStudentCode(request.studentCode()).isPresent()) {
            throw new BadRequestException("Student code already exists");
        }
        if (studentUserRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        StudentUser student = studentUserRepository.save(StudentUser.builder()
                .studentCode(request.studentCode().trim())
                .fullName(request.fullName().trim())
                .email(request.email().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.password()))
                .active(true)
                .build());
        String token = jwtService.generateToken(student.getStudentCode(), "STUDENT");
        return new StudentAuthResponse(token, student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail());
    }

    public StudentProfileResponse me(String studentCode) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new UnauthorizedException("Student not found"));
        return new StudentProfileResponse(student.getId(), student.getStudentCode(), student.getFullName(), student.getEmail());
    }
}
