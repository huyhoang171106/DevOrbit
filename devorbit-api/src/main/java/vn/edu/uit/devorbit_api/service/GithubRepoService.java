package vn.edu.uit.devorbit_api.service;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.edu.uit.devorbit_api.dto.admin.ApprovedRepoUpdateRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.entity.Course;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.TechStack;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.CourseRepository;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GithubRepoService {
    private static final Logger log = LoggerFactory.getLogger(GithubRepoService.class);
    private final GithubRepoRepository githubRepoRepository;
    private final TechStackRepository techStackRepository;
    private final CourseRepository courseRepository;

    public List<GithubRepo> getAllGithubRepo() {
        return githubRepoRepository.findAll();
    }

    public List<GithubRepo> getReposBySubject(String subjectId) {
        return githubRepoRepository.findBySubjectId(subjectId);
    }

    public List<RepoSummaryResponse> getApprovedReposByCourse(Long courseId) {
        return githubRepoRepository.findByCourseIdAndActiveTrue(courseId).stream()
                .map(this::mapToRepoSummary)
                .toList();
    }

    public List<RepoSummaryResponse> getAllApprovedRepos() {
        List<GithubRepo> repos = githubRepoRepository.findByActiveTrue();
        log.info("getAllApprovedRepos: found {} active repos", repos.size());
        return repos.stream()
                .map(this::mapToRepoSummary)
                .toList();
    }

    public List<RepoSummaryResponse> getApprovedReposByCourseAndTechStack(Long courseId, String techStack) {
        return githubRepoRepository.findByCourseIdAndActiveTrueAndTechStack(courseId, techStack).stream()
                .map(this::mapToRepoSummary)
                .toList();
    }

    @Transactional
    public RepoSummaryResponse updateApprovedRepo(Long repoId, ApprovedRepoUpdateRequest request) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));

        if (request.displayName() != null) repo.setDisplayName(trimToNull(request.displayName()));
        if (request.description() != null) repo.setDescription(request.description().trim());
        if (request.githubUrl() != null) repo.setGithubUrl(trimToNull(request.githubUrl()));
        if (request.primaryLanguage() != null) repo.setPrimaryLanguage(trimToNull(request.primaryLanguage()));
        if (request.stars() != null) repo.setStars(Math.max(0, request.stars()));
        if (request.active() != null) repo.setActive(request.active());

        if (request.techStacks() != null) {
            repo.setTechStacks(resolveTechStacks(request.techStacks()));
        }

        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new NotFoundException("Course not found: " + request.courseId()));
            repo.setCourse(course);
            repo.setSubjectId(course.getMaMH());
        }

        RepoSummaryResponse saved = mapToRepoSummary(githubRepoRepository.save(repo));
        log.info("updateApprovedRepo: updated repo id={} active={}", repoId, request.active());
        return saved;
    }

    public void deleteApprovedRepo(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));
        repo.setActive(false);
        githubRepoRepository.save(repo);
        log.info("deleteApprovedRepo: deactivated repo id={}", repoId);
    }

    public RepoSummaryResponse mapToRepoSummary(GithubRepo repo) {
        Long courseId = null;
        String courseCode = null;
        String courseName = null;
        if (repo.getCourse() != null) {
            courseId = repo.getCourse().getId();
            courseCode = repo.getCourse().getMaMH();
            courseName = repo.getCourse().getTenMH();
        }
        return new RepoSummaryResponse(
                repo.getId(),
                repo.getDisplayName(),
                repo.getDescription(),
                repo.getGithubUrl(),
                repo.getPrimaryLanguage(),
                repo.getStars() != null ? repo.getStars() : 0,
                repo.getTechStacks().stream()
                        .map(ts -> new TechStackResponse(ts.getName()))
                        .toList(),
                courseId,
                courseCode,
                courseName
        );
    }

    public Set<TechStack> resolveTechStacks(List<String> stackNames) {
        Set<TechStack> stacks = new LinkedHashSet<>();
        Set<String> seen = new LinkedHashSet<>();
        for (String rawName : stackNames) {
            String name = trimToNull(rawName);
            if (name == null) continue;
            String key = name.toLowerCase(Locale.ROOT);
            if (!seen.add(key)) continue;
            TechStack stack = techStackRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> techStackRepository.save(TechStack.builder().name(name).build()));
            stacks.add(stack);
        }
        return stacks;
    }

    private String trimToNull(String value) {
        String trimmed = value == null ? null : value.trim();
        return trimmed == null || trimmed.isEmpty() ? null : trimmed;
    }
}
