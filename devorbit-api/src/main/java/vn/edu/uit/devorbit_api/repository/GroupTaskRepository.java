package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.GroupTask;

import java.util.List;

@Repository
public interface GroupTaskRepository extends JpaRepository<GroupTask, Long> {

    List<GroupTask> findByGroupPlanIdOrderByCreatedAtAsc(Long groupPlanId);

    @Query("SELECT t FROM GroupTask t WHERE t.groupPlan.id IN :planIds AND t.assignedTo = :studentCode ORDER BY t.createdAt DESC")
    List<GroupTask> findByGroupPlanIdsAndAssignedTo(@Param("planIds") List<Long> planIds, @Param("studentCode") String studentCode);
}
