package vn.edu.uit.devorbit_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.admin.AdminStatsResponse;
import vn.edu.uit.devorbit_api.entity.CourseReview;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminStatsController {

    private final StudentUserRepository studentRepo;
    private final CourseRepository courseRepo;
    private final GithubRepoRepository githubRepoRepo;
    private final RepoCandidateRepository repoCandidateRepo;
    private final CourseReviewRepository courseReviewRepo;

    @GetMapping
    public ResponseEntity<AdminStatsResponse> getStats() {
        List<AdminStatsResponse.StudentSummary> recentStudents = studentRepo.findTop10ByOrderByIdDesc()
            .stream().map(s -> AdminStatsResponse.StudentSummary.builder()
                .id(s.getId())
                .fullName(s.getFullName())
                .studentCode(s.getStudentCode())
                .build())
            .collect(Collectors.toList());

        List<AdminStatsResponse.ReviewSummary> recentReviews = courseReviewRepo.findTop10ByOrderByCreatedAtDesc()
            .stream().map(r -> AdminStatsResponse.ReviewSummary.builder()
                .id(r.getId())
                .studentName(r.getStudent() != null ? r.getStudent().getFullName() : "Deleted")
                .courseName(r.getCourse() != null ? r.getCourse().getTenMH() : "Deleted")
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        List<AdminStatsResponse.SubmissionSummary> recentSubmissions = repoCandidateRepo.findTop10ByStatusOrderByIdDesc(RepoCandidateStatus.NEW)
            .stream().map(s -> AdminStatsResponse.SubmissionSummary.builder()
                .id(s.getId())
                .githubUrl(s.getGithubUrl())
                .courseName(s.getCourse() != null ? s.getCourse().getTenMH() : null)
                .status(s.getStatus().name())
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache().mustRevalidate())
            .body(AdminStatsResponse.builder()
                .totalStudents(studentRepo.count())
                .totalCourses(courseRepo.count())
                .totalRepos(githubRepoRepo.countByActiveTrue())
                .pendingCandidates(repoCandidateRepo.countByStatus(RepoCandidateStatus.NEW))
                .recentStudents(recentStudents)
                .recentCourseReviews(recentReviews)
                .recentSubmissions(recentSubmissions)
                .build());
    }
}
