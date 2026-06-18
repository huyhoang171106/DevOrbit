package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.ChatSession;
import java.util.List;
import java.util.UUID;

/**
 * CHAT SESSION REPOSITORY = data access for AI tutor conversation sessions.
 *
 * Each session groups multiple ChatMessages. UUID primary key for security
 * (harder to enumerate than sequential IDs).
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {

    /** All sessions for a student, most recently updated first. */
    List<ChatSession> findByStudentIdOrderByUpdatedAtDesc(Long studentId);

    /** [ADMIN] All sessions across all students, newest first. */
    List<ChatSession> findAllByOrderByCreatedAtDesc();

    /**
     * [ADMIN] All sessions with student info + message count, newest first.
     * Uses a subquery to count messages without an extra Java loop (avoids N+1).
     * LEFT JOIN FETCH ensures the student data is loaded in one query.
     *
     * Returns Object[] where:
     *   [0] = ChatSession entity
     *   [1] = Long (message count)
     */
    @Query("""
            SELECT s, (SELECT COUNT(cm.id) FROM ChatMessage cm WHERE cm.session.id = s.id)
            FROM ChatSession s LEFT JOIN FETCH s.student
            ORDER BY s.createdAt DESC
            """)
    List<Object[]> findAllWithStudentAndCount();
}
