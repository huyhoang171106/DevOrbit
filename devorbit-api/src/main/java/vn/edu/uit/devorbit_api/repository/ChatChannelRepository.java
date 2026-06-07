package vn.edu.uit.devorbit_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.uit.devorbit_api.entity.ChatChannel;

import java.util.Optional;
import java.util.List;

@Repository
public interface ChatChannelRepository extends JpaRepository<ChatChannel, Long> {
    Optional<ChatChannel> findByChannelId(String channelId);
    List<ChatChannel> findAllByOrderByTypeAscNameAsc();
}
