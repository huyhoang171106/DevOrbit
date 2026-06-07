package vn.edu.uit.devorbit_api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import vn.edu.uit.devorbit_api.dto.community.ChatChannelResponse;
import vn.edu.uit.devorbit_api.dto.community.ChatMessageRequest;
import vn.edu.uit.devorbit_api.dto.community.ChatMessageResponse;
import vn.edu.uit.devorbit_api.service.CommunityChatService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/student/community")
@RequiredArgsConstructor
public class StudentCommunityController {

    private final CommunityChatService communityChatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<ChatChannelResponse> getChannels() {
        return communityChatService.getChannels();
    }

    @GetMapping("/channels/{channelId}/messages")
    public Page<ChatMessageResponse> getMessages(
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return communityChatService.getMessages(channelId, page, size);
    }

    @MessageMapping("/chat.send/{channelId}")
    public void sendMessage(
            @DestinationVariable Long channelId,
            @Payload @Valid ChatMessageRequest request,
            Principal principal) {
        if (principal == null) {
            throw new AccessDeniedException("Community message requires authentication");
        }
        ChatMessageResponse response = communityChatService.sendMessage(principal.getName(), channelId, request);
        messagingTemplate.convertAndSend("/topic/channel/" + channelId, response);
    }
}
