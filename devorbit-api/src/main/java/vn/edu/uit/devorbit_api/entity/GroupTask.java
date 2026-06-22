package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_tasks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_plan_id", nullable = false)
    private GroupPlan groupPlan;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "assigned_to")
    private String assignedTo;

    @Column
    private LocalDate deadline;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "delete_requested", nullable = false)
    @Builder.Default
    private boolean deleteRequested = false;

    @Column(name = "delete_requested_by")
    private String deleteRequestedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
