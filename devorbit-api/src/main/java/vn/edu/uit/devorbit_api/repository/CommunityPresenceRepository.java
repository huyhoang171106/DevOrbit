package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CommunityPresence;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityPresenceRepository extends JpaRepository<CommunityPresence, Long> {
    Optional<CommunityPresence> findBySessionIdAndSubscriptionId(String sessionId, String subscriptionId);
    List<CommunityPresence> findBySessionId(String sessionId);
    List<CommunityPresence> findByChannelId(Long channelId);
    void deleteBySessionIdAndSubscriptionId(String sessionId, String subscriptionId);
    void deleteBySessionId(String sessionId);
}
