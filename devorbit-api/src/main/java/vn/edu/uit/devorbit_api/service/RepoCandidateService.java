package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.CandidateReviewRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.RepoCandidateRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepoCandidateService {
    private static final Logger log = LoggerFactory.getLogger(RepoCandidateService.class);
    private final RepoCandidateRepository repoCandidateRepository;
    private final GithubRepoRepository githubRepoRepository;
    private final GithubRepoService githubRepoService;
    private final GithubScanService githubScanService;
    private final CacheManager cacheManager;

    @Transactional(readOnly = true)
    public List<RepoCandidateResponse> getPendingCandidates(String reviewer) {
        List<RepoCandidate> candidates;
        if (reviewer != null && !reviewer.isBlank() && !reviewer.equals("all")) {
            candidates = repoCandidateRepository.findByStatusAndAssignedReviewer(
                RepoCandidateStatus.NEW, reviewer);
        } else {
            candidates = repoCandidateRepository.findByStatus(RepoCandidateStatus.NEW);
        }
        return candidates.stream()
            .map(RepoCandidateResponse::from)
            .toList();
    }

    @Transactional
    public RepoCandidateResponse approveCandidate(Long candidateId, CandidateReviewRequest request) {
        RepoCandidate candidate = repoCandidateRepository.findById(candidateId)
            .orElseThrow(() -> new NotFoundException("Candidate not found: " + candidateId));

        if (candidate.getStatus() != RepoCandidateStatus.NEW) {
            throw new BadRequestException("Candidate is already " + candidate.getStatus().name().toLowerCase());
        }

        GithubRepo repo = (candidate.getCourse() != null)
            ? githubRepoRepository.findByGithubUrlAndCourseId(candidate.getGithubUrl(), candidate.getCourse().getId())
                .orElseGet(GithubRepo::new)
            : new GithubRepo();
        repo.setId(null);

        repo.setGithubUrl(candidate.getGithubUrl());
        repo.setRepoName(candidate.getGithubName());
        repo.setDisplayName(candidate.getGithubName());
        repo.setDescription(request.description() != null ? request.description() : candidate.getDescription());
        repo.setPrimaryLanguage(candidate.getPrimaryLanguage());
        repo.setStars(candidate.getStars());
        repo.setLastPushedAt(candidate.getLastPushedAt());
        applyRepositoryContext(candidate, repo);
        if (candidate.getCourse() != null) {
            repo.setCourse(candidate.getCourse());
            repo.setSubjectId(candidate.getCourse().getMaMH());
        }
        repo.setActive(true);
        repo.setApprovedAt(LocalDateTime.now());

        if (request.techStacks() != null && !request.techStacks().isEmpty()) {
            repo.setTechStacks(githubRepoService.resolveTechStacks(request.techStacks()));
        } else if (candidate.getPrimaryLanguage() != null && !candidate.getPrimaryLanguage().isBlank()) {
            repo.setTechStacks(githubRepoService.resolveTechStacks(List.of(candidate.getPrimaryLanguage())));
        }

        githubRepoRepository.save(repo);

        evictReposCache();

        candidate.setReviewNote(request.reviewNote());
        candidate.setStatus(RepoCandidateStatus.APPROVED);
        candidate.setApprovedAt(LocalDateTime.now());
        repoCandidateRepository.save(candidate);

        log.info("approveCandidate: candidate id={} url={} -> GithubRepo id={}", candidateId, candidate.getGithubUrl(), repo.getId());

        distributeCandidates();

        return RepoCandidateResponse.from(candidate);
    }

    private void applyRepositoryContext(RepoCandidate candidate, GithubRepo repo) {
        String readmeExcerpt = candidate.getReadmeExcerpt();
        String fileTree = candidate.getFileTree();
        Boolean hasReadme = candidate.getHasReadme();

        if ((readmeExcerpt == null || fileTree == null) && candidate.getGithubOwner() != null && candidate.getGithubName() != null) {
            GithubScanService.RepositoryContext context = githubScanService.fetchRepositoryContext(
                candidate.getGithubOwner(),
                candidate.getGithubName()
            );
            if (readmeExcerpt == null) {
                readmeExcerpt = context.readmeExcerpt();
                candidate.setReadmeExcerpt(readmeExcerpt);
            }
            if (fileTree == null) {
                fileTree = context.fileTree();
                candidate.setFileTree(fileTree);
            }
            if (hasReadme == null) {
                hasReadme = context.hasReadme();
                candidate.setHasReadme(hasReadme);
            }
        }

        repo.setReadmeExcerpt(readmeExcerpt);
        repo.setFileTree(fileTree);
        repo.setHasReadme(hasReadme != null ? hasReadme : readmeExcerpt != null);
    }

    @Transactional
    public void distributeCandidates() {
        List<RepoCandidate> unassigned = repoCandidateRepository.findByStatusAndAssignedReviewer(RepoCandidateStatus.NEW, null);
        if (unassigned.isEmpty()) {
            return;
        }

        long existingBao = repoCandidateRepository.countByAssignedReviewer("Bảo");
        long existingBac = repoCandidateRepository.countByAssignedReviewer("Bắc");

        long needBao = Math.max(0, 303 - existingBao);
        long needBac = Math.max(0, 303 - existingBac);

        int idx = 0;
        for (RepoCandidate c : unassigned) {
            if (idx < needBao) {
                c.setAssignedReviewer("Bảo");
            } else if (idx < needBao + needBac) {
                c.setAssignedReviewer("Bắc");
            } else {
                c.setAssignedReviewer("An");
            }
            idx++;
        }
        repoCandidateRepository.saveAll(unassigned);
    }

    private void evictReposCache() {
        for (String name : new String[]{"allRepos", "reposByCourse", "repoById"}) {
            org.springframework.cache.Cache cache = cacheManager.getCache(name);
            if (cache != null) cache.clear();
        }
    }

    public List<vn.edu.uit.devorbit_api.dto.admin.ReviewerStatsResponse> getReviewerStats() {
        List<vn.edu.uit.devorbit_api.dto.admin.ReviewerStatsResponse> stats = new ArrayList<>();
        for (String reviewer : List.of("Bảo", "Bắc", "An")) {
            long remaining = repoCandidateRepository.countByStatusAndAssignedReviewer(
                RepoCandidateStatus.NEW, reviewer);
            long completed = repoCandidateRepository.countByStatusInAndAssignedReviewer(
                List.of(RepoCandidateStatus.APPROVED, RepoCandidateStatus.REJECTED), reviewer);
            stats.add(new vn.edu.uit.devorbit_api.dto.admin.ReviewerStatsResponse(reviewer, remaining, completed));
        }
        return stats;
    }

    @Transactional
    public RepoCandidateResponse rejectCandidate(Long candidateId) {
        RepoCandidate candidate = repoCandidateRepository.findById(candidateId)
            .orElseThrow(() -> new NotFoundException("Candidate not found: " + candidateId));

        if (candidate.getStatus() != RepoCandidateStatus.NEW) {
            throw new BadRequestException("Candidate is already " + candidate.getStatus().name().toLowerCase());
        }

        candidate.setStatus(RepoCandidateStatus.REJECTED);
        repoCandidateRepository.save(candidate);

        distributeCandidates();

        return RepoCandidateResponse.from(candidate);
    }

}
