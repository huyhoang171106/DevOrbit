package vn.edu.uit.devorbit_api.dto.admin;

import vn.edu.uit.devorbit_api.entity.RepoCandidate;

public record RepoCandidateResponse(Long id, String githubOwner, String githubName, String githubUrl, String status) {
    public static RepoCandidateResponse from(RepoCandidate candidate) {
        return new RepoCandidateResponse(
            candidate.getId(),
            candidate.getGithubOwner(),
            candidate.getGithubName(),
            candidate.getGithubUrl(),
            candidate.getStatus().name()
        );
    }
}
