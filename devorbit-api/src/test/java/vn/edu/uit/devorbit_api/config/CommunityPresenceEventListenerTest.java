package vn.edu.uit.devorbit_api.config;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;
import vn.edu.uit.devorbit_api.dto.community.ChannelPresenceResponse;
import vn.edu.uit.devorbit_api.service.CommunityPresenceService;

import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.*;

class CommunityPresenceEventListenerTest {

    private final CommunityPresenceService presenceService = mock(CommunityPresenceService.class);
    private final SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
    private final CommunityPresenceEventListener listener =
            new CommunityPresenceEventListener(presenceService, messagingTemplate);

    @Test
    void subscribeToChannelTopicTracksAndBroadcastsPresence() {
        ChannelPresenceResponse response = new ChannelPresenceResponse(7L, List.of());
        when(presenceService.subscribe("session-1", "sub-1", 7L, "24520554")).thenReturn(response);

        listener.handleSubscribe(new SessionSubscribeEvent(this,
                subscribeMessage("session-1", "sub-1", "/topic/channel/7", () -> "24520554")));

        verify(presenceService).subscribe("session-1", "sub-1", 7L, "24520554");
        verify(messagingTemplate).convertAndSend("/topic/channel/7/presence", response);
    }

    @Test
    void unsubscribeBroadcastsUpdatedPresenceWhenSubscriptionWasTracked() {
        ChannelPresenceResponse response = new ChannelPresenceResponse(7L, List.of());
        when(presenceService.unsubscribe("session-1", "sub-1")).thenReturn(response);

        listener.handleUnsubscribe(new SessionUnsubscribeEvent(this, unsubscribeMessage("session-1", "sub-1")));

        verify(messagingTemplate).convertAndSend("/topic/channel/7/presence", response);
    }

    @Test
    void disconnectBroadcastsPresenceForEveryAffectedChannel() {
        when(presenceService.disconnect("session-1")).thenReturn(List.of(7L, 8L));
        when(presenceService.presenceForChannel(7L)).thenReturn(new ChannelPresenceResponse(7L, List.of()));
        when(presenceService.presenceForChannel(8L)).thenReturn(new ChannelPresenceResponse(8L, List.of()));

        listener.handleDisconnect(new SessionDisconnectEvent(this, disconnectMessage("session-1"), "session-1", CloseStatus.NORMAL));

        verify(messagingTemplate).convertAndSend(eq("/topic/channel/7/presence"), any(ChannelPresenceResponse.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/channel/8/presence"), any(ChannelPresenceResponse.class));
    }

    private static Message<byte[]> subscribeMessage(String sessionId, String subscriptionId, String destination, Principal principal) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        accessor.setSessionId(sessionId);
        accessor.setSubscriptionId(subscriptionId);
        accessor.setDestination(destination);
        accessor.setUser(principal);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private static Message<byte[]> unsubscribeMessage(String sessionId, String subscriptionId) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.UNSUBSCRIBE);
        accessor.setSessionId(sessionId);
        accessor.setSubscriptionId(subscriptionId);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    private static Message<byte[]> disconnectMessage(String sessionId) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(SimpMessageType.DISCONNECT);
        accessor.setSessionId(sessionId);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
