package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.uit.devorbit_api.entity.PersonalTask;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonalTaskRepository extends JpaRepository<PersonalTask, Long> {

    List<PersonalTask> findByStudentCodeOrderByCreatedAtDesc(String studentCode);

    List<PersonalTask> findByStudentCodeAndCompletedOrderByCreatedAtDesc(String studentCode, boolean completed);

    @Query("SELECT t FROM PersonalTask t WHERE t.studentCode = :studentCode " +
           "AND t.deadline >= :start AND t.deadline < :end ORDER BY t.deadline ASC")
    List<PersonalTask> findByStudentCodeAndDeadlineBetween(
        @Param("studentCode") String studentCode,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
}
