package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.student.StudentTechStackRequest;
import vn.edu.uit.devorbit_api.dto.student.StudentTechStackResponse;
import vn.edu.uit.devorbit_api.entity.StudentTechStack;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.entity.TechStack;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.StudentTechStackRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class StudentTechStackService {

    private final StudentTechStackRepository techStackRepository;
    private final StudentUserRepository studentUserRepository;
    private final TechStackRepository techStackEntityRepository;

    public List<StudentTechStackResponse> getTechStacks(String studentCode) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        return techStackRepository.findByStudentIdOrderByCreatedAtDesc(student.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StudentTechStackResponse addTechStack(String studentCode, StudentTechStackRequest request) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        TechStack techStack = techStackEntityRepository.findByNameIgnoreCase(request.name().trim())
                .orElseThrow(() -> new NotFoundException("Tech stack not found: " + request.name()));

        if (techStackRepository.existsByStudentIdAndTechStackId(student.getId(), techStack.getId())) {
            throw new BadRequestException("Tech stack already added");
        }

        StudentTechStack sts = StudentTechStack.builder()
                .student(student)
                .techStack(techStack)
                .createdAt(LocalDateTime.now())
                .build();

        sts = techStackRepository.save(sts);
        return toResponse(sts);
    }

    @Transactional
    public void removeTechStack(String studentCode, Long id) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        StudentTechStack sts = techStackRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tech stack selection not found"));

        if (!sts.getStudent().getId().equals(student.getId())) {
            throw new BadRequestException("Tech stack selection does not belong to this student");
        }

        techStackRepository.delete(sts);
    }

    @Transactional
    public void removeTechStackByName(String studentCode, String name) {
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        TechStack techStack = techStackEntityRepository.findByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new NotFoundException("Tech stack not found: " + name));

        StudentTechStack sts = techStackRepository
                .findByStudentIdAndTechStackId(student.getId(), techStack.getId())
                .orElseThrow(() -> new NotFoundException("Tech stack selection not found"));

        techStackRepository.delete(sts);
    }

    private StudentTechStackResponse toResponse(StudentTechStack sts) {
        return new StudentTechStackResponse(
                sts.getId(),
                sts.getTechStack().getId(),
                sts.getTechStack().getName(),
                sts.getCreatedAt() != null ? sts.getCreatedAt().toString() : null
        );
    }
}
