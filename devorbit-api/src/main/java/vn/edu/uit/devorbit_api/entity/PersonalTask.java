package vn.edu.uit.devorbit_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "personal_tasks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_code", nullable = false)
    private String studentCode;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private LocalDateTime deadline;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;

    @Column
    private String recurrence;

    @Column(name = "recurrence_days_of_week")
    private Integer recurrenceDaysOfWeek;

    @Column(name = "recurrence_start_date")
    private LocalDate recurrenceStartDate;

    @Column(name = "recurrence_end_date")
    private LocalDate recurrenceEndDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
