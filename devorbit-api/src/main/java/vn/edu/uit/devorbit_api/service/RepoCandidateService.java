package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.admin.CandidateReviewRequest;
import vn.edu.uit.devorbit_api.dto.admin.RepoCandidateResponse;
import vn.edu.uit.devorbit_api.entity.GithubRepo;
import vn.edu.uit.devorbit_api.entity.RepoCandidate;
import vn.edu.uit.devorbit_api.entity.RepoCandidateStatus;
import vn.edu.uit.devorbit_api.entity.TechStack;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.GithubRepoRepository;
import vn.edu.uit.devorbit_api.repository.RepoCandidateRepository;
import vn.edu.uit.devorbit_api.repository.TechStackRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepoCandidateService {
    private final RepoCandidateRepository repoCandidateRepository;
    private final GithubRepoRepository githubRepoRepository;
    private final TechStackRepository techStackRepository;

    public List<RepoCandidateResponse> getPendingCandidates() {
        return repoCandidateRepository.findByStatus(RepoCandidateStatus.NEW).stream()
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

        GithubRepo repo = githubRepoRepository
            .findByGithubUrlAndCourseId(candidate.getGithubUrl(), candidate.getCourse().getId())
            .orElseGet(GithubRepo::new);

        repo.setGithubUrl(candidate.getGithubUrl());
        repo.setRepoName(candidate.getGithubName());
        repo.setDisplayName(candidate.getGithubName());
        repo.setDescription(request.description());
        repo.setCourse(candidate.getCourse());
        repo.setActive(true);

        githubRepoRepository.save(repo);

        if (repo.getId() != null) {
            techStackRepository.deleteByRepoId(repo.getId());
        }

        if (request.techStacks() != null && !request.techStacks().isEmpty()) {
            List<TechStack> techStacks = request.techStacks().stream()
                .map(stackName -> TechStack.builder()
                    .name(stackName)
                    .repo(repo)
                    .build())
                .toList();
            techStackRepository.saveAll(techStacks);
        }

        candidate.setReviewNote(request.reviewNote());
        candidate.setStatus(RepoCandidateStatus.APPROVED);
        repoCandidateRepository.save(candidate);

        return RepoCandidateResponse.from(candidate);
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

        return RepoCandidateResponse.from(candidate);
    }
}
