package vn.edu.uit.devorbit_api.service;


import lombok.RequiredArgsConstructor;
import vn.edu.uit.devorbit_api.dto.admin.ApprovedRepoUpdateRequest;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.TechStack;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
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
    private final GithubRepoRepository githubRepoRepository;
    private final TechStackRepository techStackRepository;

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
        return githubRepoRepository.findByActiveTrue().stream()
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

        return mapToRepoSummary(githubRepoRepository.save(repo));
    }

    public void deleteApprovedRepo(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));
        repo.setActive(false);
        githubRepoRepository.save(repo);
    }

    public RepoSummaryResponse mapToRepoSummary(GithubRepo repo) {
        return new RepoSummaryResponse(
                repo.getId(),
                repo.getDisplayName(),
                repo.getDescription(),
                repo.getGithubUrl(),
                repo.getPrimaryLanguage(),
                repo.getStars() != null ? repo.getStars() : 0,
                getTechStacksForRepo(repo.getId())
        );
    }

    private List<TechStackResponse> getTechStacksForRepo(Long repoId) {
        List<TechStack> stacks = techStackRepository.findByRepoIdFromJoinTable(repoId);
        if (stacks.isEmpty()) {
            stacks = techStackRepository.findByRepoId(repoId);
        }
        return stacks.stream()
                .map(techStack -> new TechStackResponse(techStack.getName()))
                .toList();
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
