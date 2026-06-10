package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.RoadmapPhase;
import java.util.List;

@Repository
public interface RoadmapPhaseRepository extends JpaRepository<RoadmapPhase, Long> {
    List<RoadmapPhase> findByRoadmapIdOrderBySortOrderAsc(Long roadmapId);

    void deleteByRoadmapId(Long roadmapId);
}
