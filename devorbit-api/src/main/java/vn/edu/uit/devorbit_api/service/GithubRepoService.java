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

import java.util.List;

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

    public RepoSummaryResponse updateApprovedRepo(Long repoId, ApprovedRepoUpdateRequest request) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));

        if (request.displayName() != null) repo.setDisplayName(request.displayName());
        if (request.description() != null) repo.setDescription(request.description());
        if (request.active() != null) repo.setActive(request.active());

        githubRepoRepository.save(repo);

        if (request.techStacks() != null) {
            techStackRepository.deleteByRepoId(repoId);
            for (String stackName : request.techStacks()) {
                techStackRepository.save(TechStack.builder()
                        .name(stackName)
                        .repo(repo)
                        .build());
            }
        }

        return mapToRepoSummary(repo);
    }

    public void deleteApprovedRepo(Long repoId) {
        GithubRepo repo = githubRepoRepository.findById(repoId)
                .orElseThrow(() -> new NotFoundException("Repo not found: " + repoId));
        repo.setActive(false);
        githubRepoRepository.save(repo);
    }

    private RepoSummaryResponse mapToRepoSummary(GithubRepo repo) {
        return new RepoSummaryResponse(
                repo.getId(),
                repo.getDisplayName(),
                repo.getDescription(),
                repo.getGithubUrl(),
                repo.getPrimaryLanguage(),
                repo.getStars(),
                getTechStacksForRepo(repo.getId())
        );
    }

    private List<TechStackResponse> getTechStacksForRepo(Long repoId) {
        return techStackRepository.findByRepoId(repoId).stream()
                .map(techStack -> new TechStackResponse(techStack.getName()))
                .toList();
    }
}
