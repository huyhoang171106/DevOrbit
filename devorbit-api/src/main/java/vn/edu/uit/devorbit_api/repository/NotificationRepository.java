package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.Notification;

import java.util.List;

/**
 * NOTIFICATION REPOSITORY = data access for system notifications.
 *
 * Notifications are created by the system (e.g., "New GitHub repo added to SE101").
 * They have a simple isRead flag for tracking what the admin has seen.
 *
 * @Modifying + @Query is used for markAllAsRead because it's a BULK UPDATE
 * operation — updating many rows at once without loading them into memory.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** All notifications, newest first. */
    List<Notification> findAllByOrderByCreatedAtDesc();

    /** Unread notifications only, newest first. */
    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();

    /** Count of unread notifications (for badge on the admin UI). */
    long countByIsReadFalse();

    /**
     * Mark ALL notifications as read in a single UPDATE query.
     * Returns the number of rows updated.
     * Uses @Modifying because it's an UPDATE, not a SELECT.
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.isRead = false")
    int markAllAsRead();
}
