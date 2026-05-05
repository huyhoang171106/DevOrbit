package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RoadmapItem;
import java.util.List;

@Repository
public interface RoadmapItemRepository extends JpaRepository<RoadmapItem, Long> {
    List<RoadmapItem> findByPhaseIdOrderBySortOrderAsc(Long phaseId);

    void deleteByPhaseId(Long phaseId);
}
