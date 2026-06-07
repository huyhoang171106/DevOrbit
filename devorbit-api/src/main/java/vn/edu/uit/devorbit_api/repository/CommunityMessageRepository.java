package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.CommunityMessage;

@Repository
public interface CommunityMessageRepository extends JpaRepository<CommunityMessage, Long> {
    Page<CommunityMessage> findByChannelIdOrderByCreatedAtDesc(Long channelId, Pageable pageable);
}
