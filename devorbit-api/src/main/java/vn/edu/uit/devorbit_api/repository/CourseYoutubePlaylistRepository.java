package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CourseYoutubePlaylist;
import java.util.List;

@Repository
public interface CourseYoutubePlaylistRepository extends JpaRepository<CourseYoutubePlaylist, Long> {
    List<CourseYoutubePlaylist> findByCourseIdOrderByCreatedAtDesc(Long courseId);

    void deleteByCourseId(Long courseId);
}
