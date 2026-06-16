package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.community.*;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.event.NotificationEvent;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.exception.UnauthorizedException;
import vn.edu.uit.devorbit_api.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final ApplicationEventPublisher eventPublisher;
    private final RepoReviewRepository repoReviewRepository;
    private final RepoVoteRepository repoVoteRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final GithubRepoRepository githubRepoRepository;
    private final CourseRepository courseRepository;
    private final StudentUserRepository studentUserRepository;

    @Transactional
    public ReviewResponse upsertRepoReview(String studentCode, Long repoId, ReviewRequest request) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repository not found"));
        if (!repo.isActive()) {
            throw new NotFoundException("Repository not found");
        }
        StudentUser student = findStudent(studentCode);
        RepoReview review = repoReviewRepository.findByRepoIdAndStudentId(repoId, student.getId())
                .orElseGet(() -> RepoReview.builder().repo(repo).student(student).build());
        review.setRating(request.rating());
        review.setComment(request.comment());
        RepoReview saved = repoReviewRepository.save(review);
        eventPublisher.publishEvent(new NotificationEvent(
            "REVIEW_REPO",
            "Đánh giá mới cho repository: " + repo.getDisplayName(),
            "/admin/reviews?tab=repo"
        ));
        return toRepoReviewResponse(saved);
    }

    @Transactional
    public void deleteRepoReview(String studentCode, Long repoId) {
        githubRepoRepository.findById(repoId)
                .filter(GithubRepo::isActive)
                .orElseThrow(() -> new NotFoundException("Repository not found"));
        StudentUser student = findStudent(studentCode);
        repoReviewRepository.findByRepoIdAndStudentId(repoId, student.getId())
                .ifPresent(repoReviewRepository::delete);
    }

    @Transactional
    public RepoVoteResponse voteRepo(String studentCode, Long repoId, RepoVoteRequest request) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repository not found"));
        if (!repo.isActive()) {
            throw new NotFoundException("Repository not found");
        }
        StudentUser student = findStudent(studentCode);
        int value = request.voteValue();
        if (value < -1 || value > 1) {
            throw new BadRequestException("voteValue must be -1, 0, or 1");
        }

        RepoVote existing = repoVoteRepository.findByRepoIdAndStudentId(repoId, student.getId()).orElse(null);
        if (value == 0) {
            if (existing != null) {
                repoVoteRepository.delete(existing);
            }
            return new RepoVoteResponse(repoId, student.getId(), 0, voteScore(repoId));
        }

        RepoVote vote = existing != null ? existing : RepoVote.builder().repo(repo).student(student).build();
        vote.setVoteValue(value);
        repoVoteRepository.save(vote);
        return new RepoVoteResponse(repoId, student.getId(), value, voteScore(repoId));
    }

    public RepoSocialInfoResponse getRepoSocialInfo(Long repoId) {
        githubRepoRepository.findById(repoId)
                .filter(GithubRepo::isActive)
                .orElseThrow(() -> new NotFoundException("Repository not found"));
        return new RepoSocialInfoResponse(
                repoId,
                voteScore(repoId),
                average(repoReviewRepository.averageRatingByRepoId(repoId)),
                repoReviewRepository.findByRepoIdOrderByUpdatedAtDesc(repoId)
                        .stream()
                        .map(this::toRepoReviewResponse)
                        .toList());
    }

    @Transactional
    public ReviewResponse upsertCourseReview(String studentCode, Long courseId, ReviewRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        StudentUser student = findStudent(studentCode);
        CourseReview review = courseReviewRepository.findByCourseIdAndStudentId(courseId, student.getId())
                .orElseGet(() -> CourseReview.builder().course(course).student(student).build());
        review.setRating(request.rating());
        review.setComment(request.comment());
        CourseReview saved = courseReviewRepository.save(review);
        eventPublisher.publishEvent(new NotificationEvent(
            "REVIEW_COURSE",
            "Đánh giá mới cho môn học: " + course.getTenMH(),
            "/admin/reviews?tab=course"
        ));
        return toCourseReviewResponse(saved);
    }

    @Transactional
    public void deleteCourseReview(String studentCode, Long courseId) {
        StudentUser student = findStudent(studentCode);
        courseReviewRepository.findByCourseIdAndStudentId(courseId, student.getId())
                .ifPresent(courseReviewRepository::delete);
    }

    public ReviewSummaryResponse getCourseReviews(Long courseId) {
        courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        return new ReviewSummaryResponse(
                courseId,
                average(courseReviewRepository.averageRatingByCourseId(courseId)),
                courseReviewRepository.findByCourseIdOrderByUpdatedAtDesc(courseId)
                        .stream()
                        .map(this::toCourseReviewResponse)
                        .toList());
    }

    private StudentUser findStudent(String studentCode) {
        return studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));
    }

    private int voteScore(Long repoId) {
        Integer score = repoVoteRepository.sumVoteValueByRepoId(repoId);
        return score == null ? 0 : score;
    }

    private double average(Double value) {
        return value == null ? 0.0 : value;
    }

    private ReviewResponse toRepoReviewResponse(RepoReview review) {
        return new ReviewResponse(
                review.getId(),
                review.getRepo().getId(),
                review.getStudent().getId(),
                review.getStudent().getFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt() != null ? review.getCreatedAt().toString() : null,
                review.getUpdatedAt() != null ? review.getUpdatedAt().toString() : null);
    }

    private ReviewResponse toCourseReviewResponse(CourseReview review) {
        return new ReviewResponse(
                review.getId(),
                review.getCourse().getId(),
                review.getStudent().getId(),
                review.getStudent().getFullName(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt() != null ? review.getCreatedAt().toString() : null,
                review.getUpdatedAt() != null ? review.getUpdatedAt().toString() : null);
    }
}
