package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * COURSE YOUTUBE PLAYLIST = a video playlist linked to a course.
 *
 * Maps to the "course_youtube_playlists" table.
 * Each playlist belongs to ONE course.
 *
 * Fetch via: GET /api/courses/{courseId}/videos
 */
@Entity
@Table(name = "course_youtube_playlists")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseYoutubePlaylist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The course this playlist belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /** Playlist title (e.g., "SE101 - Lecture 1-10") */
    @Column(nullable = false, length = 255)
    private String title;

    /** YouTube URL */
    @Column(nullable = false, length = 255)
    private String url;

    /** What this playlist covers */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Name of the YouTube channel that created the playlist */
    @Column(name = "channel_name", length = 255)
    private String channelName;

    /** When this playlist was added */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
