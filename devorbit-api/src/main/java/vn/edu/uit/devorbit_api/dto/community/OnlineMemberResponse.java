package vn.edu.uit.devorbit_api.dto.community;

/**
 * Response DTO for an individual online member within a channel.
 * Embedded inside {@link ChannelPresenceResponse}.
 *
 * @param studentId   Internal ID of the student.
 * @param studentCode Student's institutional code (e.g. "21520101").
 * @param displayName Display name shown in the UI.
 * @param avatar      URL or path to the student's avatar image.
 */
public record OnlineMemberResponse(
        Long studentId,
        String studentCode,
        String displayName,
        String avatar
) {
}
