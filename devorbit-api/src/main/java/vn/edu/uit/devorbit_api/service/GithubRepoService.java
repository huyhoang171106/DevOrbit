package vn.edu.uit.devorbit_api.service;


import lombok.RequiredArgsConstructor;
import vn.edu.uit.devorbit_api.dto.publicapi.RepoSummaryResponse;
import vn.edu.uit.devorbit_api.dto.publicapi.TechStackResponse;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
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
                .map(repo -> new RepoSummaryResponse(
                        repo.getId(),
                        repo.getDisplayName(),
                        repo.getDescription(),
                        repo.getGithubUrl(),
                        repo.getPrimaryLanguage(),
                        repo.getStars(),
                        getTechStacksForRepo(repo.getId())
                ))
                .toList();
    }
    private List<TechStackResponse> getTechStacksForRepo(Long repoId) {
        return techStackRepository.findByRepoId(repoId).stream()
                .map(techStack -> new TechStackResponse(techStack.getName()))
                .toList();
    }
}
