package vn.edu.uit.devorbit_api.dto.student;

/**
 * STUDENT VOTE RESPONSE = a repo the student voted on, with vote info.
 *
 * @param repoId     ID of the repository.
 * @param repoName   Display name of the repository.
 * @param githubUrl  Full GitHub URL of the repository.
 * @param voteValue  1 = upvote, -1 = downvote.
 * @param createdAt  ISO timestamp of when the vote was cast.
 */
public record StudentVoteResponse(
    Long repoId,
    String repoName,
    String githubUrl,
    int voteValue,
    String createdAt
) {}
