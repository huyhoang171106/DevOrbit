package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.constant.MemberStatus;
import vn.edu.uit.devorbit_api.entity.GroupPlanMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupPlanMemberRepository extends JpaRepository<GroupPlanMember, Long> {

    List<GroupPlanMember> findByGroupPlanId(Long groupPlanId);

    Optional<GroupPlanMember> findByGroupPlanIdAndStudentCode(Long groupPlanId, String studentCode);

    boolean existsByGroupPlanIdAndStudentCodeAndStatus(Long groupPlanId, String studentCode, MemberStatus status);

    List<GroupPlanMember> findByStudentCodeAndStatus(String studentCode, MemberStatus status);

    long countByGroupPlanIdAndStatus(Long groupPlanId, MemberStatus status);
}
