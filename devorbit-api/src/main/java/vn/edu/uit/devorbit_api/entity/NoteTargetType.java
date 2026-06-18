package vn.edu.uit.devorbit_api.entity;

/**
 * WHAT a student's note is ABOUT.
 *
 * COURSE — Note attached to a course (e.g., summary of a lecture).
 *          targetId = Course.id
 *
 * REPO   — Note attached to a GitHub repo (e.g., code review notes).
 *          targetId = GithubRepo.id
 *
 * NONE   — General-purpose note with no specific attachment.
 *          targetId is null.
 *
 * Used together with Note.targetId to locate the referenced object.
 */
public enum NoteTargetType {
    COURSE,
    REPO,
    NONE
}
