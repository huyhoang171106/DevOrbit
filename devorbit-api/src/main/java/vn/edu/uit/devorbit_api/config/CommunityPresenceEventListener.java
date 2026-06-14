package vn.edu.uit.devorbit_api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import vn.edu.uit.devorbit_api.dto.community.ChannelPresenceResponse;
import vn.edu.uit.devorbit_api.service.CommunityPresenceService;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class CommunityPresenceEventListener {

    private static final Pattern CHANNEL_TOPIC = Pattern.compile("^/topic/channel/(\\d+)$");

    private final CommunityPresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = accessor(event.getMessage());
        if (accessor == null || accessor.getUser() == null) {
            return;
        }

        Optional<Long> channelId = parseChannelId(accessor.getDestination());
        if (channelId.isEmpty()) {
            return;
        }

        ChannelPresenceResponse presence = presenceService.subscribe(
                accessor.getSessionId(),
                accessor.getSubscriptionId(),
                channelId.get(),
                accessor.getUser().getName());
        broadcast(presence);
    }

    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = accessor(event.getMessage());
        if (accessor == null) {
            return;
        }

        ChannelPresenceResponse presence = presenceService.unsubscribe(accessor.getSessionId(), accessor.getSubscriptionId());
        if (presence != null) {
            broadcast(presence);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        presenceService.disconnect(event.getSessionId()).stream()
                .map(presenceService::presenceForChannel)
                .forEach(this::broadcast);
    }

    private StompHeaderAccessor accessor(Message<byte[]> message) {
        return StompHeaderAccessor.wrap(message);
    }

    private Optional<Long> parseChannelId(String destination) {
        if (destination == null) {
            return Optional.empty();
        }
        Matcher matcher = CHANNEL_TOPIC.matcher(destination);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        return Optional.of(Long.parseLong(matcher.group(1)));
    }

    private void broadcast(ChannelPresenceResponse presence) {
        messagingTemplate.convertAndSend("/topic/channel/" + presence.channelId() + "/presence", presence);
    }
}
