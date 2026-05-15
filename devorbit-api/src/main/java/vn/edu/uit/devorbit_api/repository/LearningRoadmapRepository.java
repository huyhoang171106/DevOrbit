package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.LearningRoadmap;
import java.util.List;

@Repository
public interface LearningRoadmapRepository extends JpaRepository<LearningRoadmap, Long> {
    List<LearningRoadmap> findByStudentIdOrderByUpdatedAtDesc(Long studentId);

    List<LearningRoadmap> findAllByOrderByUpdatedAtDesc();
}
