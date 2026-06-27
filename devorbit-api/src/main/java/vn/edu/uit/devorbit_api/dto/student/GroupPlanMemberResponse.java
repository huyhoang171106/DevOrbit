package vn.edu.uit.devorbit_api.dto.student;

import vn.edu.uit.devorbit_api.constant.MemberStatus;
import vn.edu.uit.devorbit_api.entity.GroupPlanMember;

public record GroupPlanMemberResponse(
    Long id,
    Long groupPlanId,
    String studentCode,
    String status,
    String invitedAt,
    String respondedAt
) {
    public static GroupPlanMemberResponse from(GroupPlanMember m) {
        return new GroupPlanMemberResponse(
            m.getId(),
            m.getGroupPlan().getId(),
            m.getStudentCode(),
            m.getStatus().name(),
            m.getInvitedAt() != null ? m.getInvitedAt().toString() : null,
            m.getRespondedAt() != null ? m.getRespondedAt().toString() : null
        );
    }
}
