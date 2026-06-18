package vn.edu.uit.devorbit_api.dto.admin;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for creating or updating a YouTube playlist resource.
 *
 * <p>A "YouTube playlist" is a curated collection of video tutorials
 * embedded from YouTube, linked to a specific course. The {@code title}
 * and {@code url} are required; optional fields include a free-text
 * {@code description} and the {@code channelName} of the YouTube
 * channel that owns the playlist.</p>
 *
 * <p><b>Used by:</b><ul>
 *   <li>{@code POST /api/admin/courses/{courseId}/resources/youtube-playlists}
 *       — create a new playlist entry for the course.</li>
 *   <li>{@code PUT /api/admin/courses/{courseId}/resources/youtube-playlists/{id}}
 *       — update an existing playlist entry.</li>
 * </ul></p>
 *
 * <p><b>Example JSON (create):</b>
 * <pre>{@code
 * {
 *   "title": "Java Programming Full Course",
 *   "url": "https://www.youtube.com/playlist?list=PLA0cL0YhG8",
 *   "description": "Comprehensive Java course covering OOP, Collections, Streams",
 *   "channelName": "Programming with Mosh"
 * }
 * }</pre></p>
 *
 * @param title       Playlist title (required). Must not be blank.
 *                    Example: {@code "Java Programming Full Course"}.
 * @param url         Full YouTube playlist URL (required). Must not be blank.
 *                    Example: {@code "https://www.youtube.com/playlist?list=PLA0cL0YhG8"}.
 * @param description Optional summary of the playlist content. May be {@code null}.
 * @param channelName Optional name of the YouTube channel. May be {@code null}.
 *                    Example: {@code "Programming with Mosh"}.
 */
public record YoutubePlaylistRequest(
    @NotBlank String title,
    @NotBlank String url,
    String description,
    String channelName
) {}
