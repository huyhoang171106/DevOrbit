package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.ChatSession;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for ChatSession entities.
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    List<ChatSession> findByStudentIdOrderByUpdatedAtDesc(Long studentId);
    List<ChatSession> findAllByOrderByCreatedAtDesc();

    @Query("""
            SELECT s, (SELECT COUNT(cm.id) FROM ChatMessage cm WHERE cm.session.id = s.id)
            FROM ChatSession s LEFT JOIN FETCH s.student
            ORDER BY s.createdAt DESC
            """)
    List<Object[]> findAllWithStudentAndCount();
}
