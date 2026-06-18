package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseYoutubePlaylist;
import java.util.List;

/**
 * COURSE YOUTUBE PLAYLIST REPOSITORY = data access for video playlists linked to courses.
 */
@Repository
public interface CourseYoutubePlaylistRepository extends JpaRepository<CourseYoutubePlaylist, Long> {

    /** All YouTube playlists for a course, newest first. */
    List<CourseYoutubePlaylist> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    /** Cascade cleanup when course is deleted. */
    void deleteByCourseId(Long courseId);
}
