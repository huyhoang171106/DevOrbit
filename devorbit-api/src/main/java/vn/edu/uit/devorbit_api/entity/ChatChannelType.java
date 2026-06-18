package vn.edu.uit.devorbit_api.entity;

/**
 * TYPES OF CHAT CHANNELS in the community.
 *
 * GENERAL    — Open chat for everyone (lobby / general discussion)
 * COURSE     — Channel tied to a specific course (e.g., "SE101 Chat")
 * TECH_STACK — Channel for discussing a specific technology
 *              (e.g., "React", "Spring Boot")
 *
 * The type determines how referenceId is used:
 *   GENERAL    → referenceId is null
 *   COURSE     → referenceId is the course code (maMH)
 *   TECH_STACK → referenceId is the tech stack name
 */
public enum ChatChannelType {
    GENERAL,
    COURSE,
    TECH_STACK
}
