package vn.edu.uit.devorbit_api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.uit.devorbit_api.dto.community.ChatChannelResponse;
import vn.edu.uit.devorbit_api.dto.community.ChatMessageRequest;
import vn.edu.uit.devorbit_api.dto.community.ChatMessageResponse;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.event.NotificationEvent;
import vn.edu.uit.devorbit_api.exception.BadRequestException;
import vn.edu.uit.devorbit_api.exception.NotFoundException;
import vn.edu.uit.devorbit_api.repository.*;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class CommunityChatService {

    private final ApplicationEventPublisher eventPublisher;
    private final NotificationRepository notificationRepository;
    private final ChatChannelRepository channelRepository;
    private final CommunityMessageRepository messageRepository;
    private final StudentUserRepository studentUserRepository;
    private final CourseRepository courseRepository;
    private final TechStackRepository techStackRepository;

    private final AtomicBoolean syncDone = new AtomicBoolean(false);

    public List<ChatChannelResponse> getChannels() {
        if (syncDone.compareAndSet(false, true)) {
            syncChannels();
        }
        return channelRepository.findByActiveTrueOrderByTypeAscNameAsc()
                .stream()
                .map(this::toChannelResponse)
                .toList();
    }

    public void syncChannels() {
        java.util.List<ChatChannel> allChannels = channelRepository.findAll();
        java.util.Map<String, ChatChannel> channelMap = new java.util.HashMap<>();
        for (ChatChannel channel : allChannels) {
            channelMap.put(channel.getChannelId(), channel);
        }

        ensureChannelInMemory("general", "Kenh chung", ChatChannelType.GENERAL, null, true, channelMap);
        ensureChannelInMemory("study", "Hoc tap", ChatChannelType.GENERAL, null, true, channelMap);
        ensureChannelInMemory("relax", "Giai tri", ChatChannelType.GENERAL, null, true, channelMap);

        courseRepository.findAll().forEach(course -> {
            String code = course.getMaMH() != null ? course.getMaMH() : String.valueOf(course.getId());
            ensureChannelInMemory("course-" + slug(code), course.getTenMH(), ChatChannelType.COURSE, String.valueOf(course.getId()), course.isOpen(), channelMap);
        });

        techStackRepository.findAllDistinctOrderByName().forEach(techStack ->
                ensureChannelInMemory("tech-" + slug(techStack.getName()), techStack.getName(), ChatChannelType.TECH_STACK, String.valueOf(techStack.getId()), true, channelMap));
    }

    private void ensureChannelInMemory(String channelId, String name, ChatChannelType type, String referenceId, boolean active, java.util.Map<String, ChatChannel> channelMap) {
        ChatChannel channel = channelMap.get(channelId);
        if (channel != null) {
            if (channel.isActive() != active) {
                channel.setActive(active);
                channelRepository.save(channel);
            }
            return;
        }
        channelRepository.save(ChatChannel.builder()
                .channelId(channelId)
                .name(name == null || name.isBlank() ? channelId : name)
                .type(type)
                .referenceId(referenceId)
                .active(active)
                .build());
    }

    public Page<ChatMessageResponse> getMessages(Long channelId, int page, int size) {
        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel not found");
        }
        int safeSize = Math.max(1, Math.min(size, 100));
        return messageRepository.findByChannelIdOrderByCreatedAtDesc(channelId, PageRequest.of(Math.max(page, 0), safeSize))
                .map(this::toMessageResponse);
    }

    @Transactional
    public void createChannel(ChatChannelType type, String referenceId, String name) {
        String channelId = switch (type) {
            case COURSE -> "course-" + slug(referenceId);
            case TECH_STACK -> "tech-" + slug(name);
            case GENERAL -> slug(name);
        };
        if (channelRepository.findByChannelId(channelId).isEmpty()) {
            channelRepository.save(ChatChannel.builder()
                    .channelId(channelId)
                    .name(name)
                    .type(type)
                    .referenceId(referenceId)
                    .active(true)
                    .build());
        }
    }

    @Transactional
    public void deactivateByReference(ChatChannelType type, String referenceId) {
        channelRepository.findByTypeAndReferenceId(type, referenceId)
                .forEach(channel -> {
                    channel.setActive(false);
                    channelRepository.save(channel);
                });
    }

    public boolean channelHasMessages(Long channelId) {
        return messageRepository.existsByChannelId(channelId);
    }

    @Transactional
    public ChatMessageResponse sendMessage(String studentCode, Long channelId, ChatMessageRequest request) {
        ChatChannel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel not found"));
        if (!channel.isActive()) {
            throw new BadRequestException("Channel is not active");
        }
        StudentUser student = studentUserRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new NotFoundException("Student not found"));

        boolean hasContent = request.content() != null && !request.content().isBlank();
        boolean hasImage = request.imageUrl() != null && !request.imageUrl().isBlank();
        if (!hasContent && !hasImage) {
            throw new BadRequestException("Message must have content or image");
        }
        if (hasContent && hasImage) {
            throw new BadRequestException("Cannot send both text and image in same message");
        }

        CommunityMessage message = CommunityMessage.builder()
                .channel(channel)
                .student(student)
                .content(hasContent ? request.content().trim() : "")
                .imageUrl(hasImage ? request.imageUrl().trim() : null)
                .build();

        ChatMessageResponse response = toMessageResponse(messageRepository.save(message));

        String targetUrl = "/admin/community?ch=" + channel.getId();
        boolean hasExisting = notificationRepository.findByIsReadFalseOrderByCreatedAtDesc()
                .stream().anyMatch(n -> targetUrl.equals(n.getTargetUrl()));
        if (!hasExisting) {
            eventPublisher.publishEvent(new NotificationEvent(
                "COMMUNITY_CHAT",
                "Có tin nhắn mới trong: " + channel.getName(),
                targetUrl
            ));
        }

        return response;
    }



    private ChatChannelResponse toChannelResponse(ChatChannel channel) {
        return new ChatChannelResponse(channel.getId(), channel.getChannelId(), channel.getName(), channel.getType(), channel.getReferenceId());
    }

    private ChatMessageResponse toMessageResponse(CommunityMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getChannel().getId(),
                message.getStudent().getId(),
                message.getStudent().getFullName(),
                message.getStudent().getAvatar(),
                message.getContent(),
                message.getImageUrl(),
                message.getCreatedAt() != null ? message.getCreatedAt().toString() : null,
                message.isDeleted());
    }

    private String slug(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String slug = normalized.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        return slug.isBlank() ? "unknown" : slug;
    }
}
