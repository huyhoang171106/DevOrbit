package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.uit.devorbit_api.constant.MemberStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_plan_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_plan_id", "student_code"})
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupPlanMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_plan_id", nullable = false)
    private GroupPlan groupPlan;

    @Column(name = "student_code", nullable = false)
    private String studentCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberStatus status = MemberStatus.PENDING;

    @Column(name = "invited_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime invitedAt = LocalDateTime.now();

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
}
