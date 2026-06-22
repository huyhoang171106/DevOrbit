package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.GroupTask;

import java.util.List;

@Repository
public interface GroupTaskRepository extends JpaRepository<GroupTask, Long> {

    List<GroupTask> findByGroupPlanIdOrderByCreatedAtAsc(Long groupPlanId);
}
