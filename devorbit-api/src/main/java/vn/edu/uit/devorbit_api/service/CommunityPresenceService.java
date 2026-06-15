package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.uit.devorbit_api.dto.community.ChannelPresenceResponse;
import vn.edu.uit.devorbit_api.dto.community.OnlineMemberResponse;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CommunityPresenceService {

    private final StudentUserRepository studentUserRepository;
    private final Map<String, PresenceSubscription> subscriptions = new HashMap<>();

    public synchronized ChannelPresenceResponse subscribe(String sessionId, String subscriptionId, Long channelId, String studentCode) {
        String key = subscriptionKey(sessionId, subscriptionId);
        subscriptions.put(key, new PresenceSubscription(sessionId, subscriptionId, channelId, studentCode));
        return presenceForChannel(channelId);
    }

    public synchronized ChannelPresenceResponse unsubscribe(String sessionId, String subscriptionId) {
        PresenceSubscription removed = subscriptions.remove(subscriptionKey(sessionId, subscriptionId));
        return removed == null ? null : presenceForChannel(removed.channelId());
    }

    public synchronized List<Long> disconnect(String sessionId) {
        Set<Long> affectedChannels = new LinkedHashSet<>();
        Iterator<Map.Entry<String, PresenceSubscription>> iterator = subscriptions.entrySet().iterator();
        while (iterator.hasNext()) {
            PresenceSubscription subscription = iterator.next().getValue();
            if (Objects.equals(subscription.sessionId(), sessionId)) {
                affectedChannels.add(subscription.channelId());
                iterator.remove();
            }
        }
        return List.copyOf(affectedChannels);
    }

    public synchronized ChannelPresenceResponse presenceForChannel(Long channelId) {
        Map<String, OnlineMemberResponse> membersByStudentCode = new LinkedHashMap<>();
        subscriptions.values().stream()
                .filter(subscription -> Objects.equals(subscription.channelId(), channelId))
                .forEach(subscription -> membersByStudentCode.putIfAbsent(
                        subscription.studentCode(),
                        toOnlineMember(subscription.studentCode())));

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

    private String subscriptionKey(String sessionId, String subscriptionId) {
        return sessionId + "::" + subscriptionId;
    }

    private record PresenceSubscription(
            String sessionId,
            String subscriptionId,
            Long channelId,
            String studentCode
    ) {
    }
}
