package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.GroupPlan;

import java.util.List;

@Repository
public interface GroupPlanRepository extends JpaRepository<GroupPlan, Long> {

    @Query("SELECT gp FROM GroupPlan gp WHERE gp.active = true AND (gp.creatorStudentCode = :studentCode OR gp.id IN " +
           "(SELECT gpm.groupPlan.id FROM GroupPlanMember gpm WHERE gpm.studentCode = :studentCode AND gpm.status = 'ACCEPTED'))")
    List<GroupPlan> findActiveByStudentCode(@Param("studentCode") String studentCode);
}
