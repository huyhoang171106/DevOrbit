package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.StudentNotification;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentNotificationRepository extends JpaRepository<StudentNotification, Long> {

    List<StudentNotification> findByStudentCodeOrderByCreatedAtDesc(String studentCode);

    long countByStudentCodeAndIsReadFalse(String studentCode);

    @Modifying
    @Query("UPDATE StudentNotification n SET n.isRead = true, n.readAt = :now WHERE n.studentCode = :studentCode AND n.isRead = false")
    int markAllAsRead(@Param("studentCode") String studentCode, @Param("now") LocalDateTime now);

    @Modifying
    @Query("UPDATE StudentNotification n SET n.isRead = true, n.readAt = :now WHERE n.id = :id AND n.studentCode = :studentCode")
    int markAsRead(@Param("id") Long id, @Param("studentCode") String studentCode, @Param("now") LocalDateTime now);
}
