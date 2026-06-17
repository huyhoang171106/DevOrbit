package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.community.ChannelPresenceResponse;
import vn.edu.uit.devorbit_api.dto.community.OnlineMemberResponse;
import vn.edu.uit.devorbit_api.entity.ChatChannel;
import vn.edu.uit.devorbit_api.entity.CommunityPresence;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.repository.ChatChannelRepository;
import vn.edu.uit.devorbit_api.repository.CommunityPresenceRepository;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityPresenceService {

    private final StudentUserRepository studentUserRepository;
    private final ChatChannelRepository chatChannelRepository;
    private final CommunityPresenceRepository presenceRepository;

    public ChannelPresenceResponse subscribe(String sessionId, String subscriptionId, Long channelId, String studentCode) {
        ChatChannel channel = chatChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));

        presenceRepository.deleteBySessionIdAndSubscriptionId(sessionId, subscriptionId);

        CommunityPresence presence = CommunityPresence.builder()
                .sessionId(sessionId)
                .subscriptionId(subscriptionId)
                .channel(channel)
                .studentCode(studentCode)
                .build();
        presenceRepository.save(presence);

        return presenceForChannel(channelId);
    }

    public ChannelPresenceResponse unsubscribe(String sessionId, String subscriptionId) {
        Optional<CommunityPresence> existing = presenceRepository.findBySessionIdAndSubscriptionId(sessionId, subscriptionId);
        if (existing.isPresent()) {
            CommunityPresence presence = existing.get();
            presenceRepository.delete(presence);
            return presenceForChannel(presence.getChannel().getId());
        }
        return null;
    }

    public List<Long> disconnect(String sessionId) {
        List<CommunityPresence> presences = presenceRepository.findBySessionId(sessionId);
        List<Long> affectedChannels = presences.stream()
                .map(p -> p.getChannel().getId())
                .distinct()
                .toList();
        presenceRepository.deleteBySessionId(sessionId);
        return affectedChannels;
    }

    @Transactional(readOnly = true)
    public ChannelPresenceResponse presenceForChannel(Long channelId) {
        List<CommunityPresence> presences = presenceRepository.findByChannelId(channelId);
        Map<String, OnlineMemberResponse> membersByStudentCode = new LinkedHashMap<>();
        
        for (CommunityPresence presence : presences) {
            membersByStudentCode.putIfAbsent(
                    presence.getStudentCode(),
                    toOnlineMember(presence.getStudentCode())
            );
        }

        List<OnlineMemberResponse> members = membersByStudentCode.values().stream()
                .sorted(Comparator.comparing(OnlineMemberResponse::displayName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        return new ChannelPresenceResponse(channelId, members);
    }

    private OnlineMemberResponse toOnlineMember(String studentCode) {
        return studentUserRepository.findByStudentCode(studentCode)
                .map(this::toOnlineMember)
                .orElseGet(() -> new OnlineMemberResponse(null, studentCode, studentCode, null));
    }

    private OnlineMemberResponse toOnlineMember(StudentUser student) {
        String displayName = student.getFullName() == null || student.getFullName().isBlank()
                ? student.getStudentCode()
                : student.getFullName();
        return new OnlineMemberResponse(student.getId(), student.getStudentCode(), displayName, student.getAvatar());
    }
}
