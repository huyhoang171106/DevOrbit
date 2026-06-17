package vn.edu.uit.devorbit_api.community;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.uit.devorbit_api.controller.PublicSocialController;
import vn.edu.uit.devorbit_api.controller.StudentCommunityController;
import vn.edu.uit.devorbit_api.controller.StudentSocialController;
import vn.edu.uit.devorbit_api.dto.community.ChatMessageRequest;
import vn.edu.uit.devorbit_api.dto.community.RepoVoteRequest;
import vn.edu.uit.devorbit_api.dto.community.ReviewRequest;
import vn.edu.uit.devorbit_api.entity.*;
import vn.edu.uit.devorbit_api.repository.*;
import vn.edu.uit.devorbit_api.service.CommunityChatService;
import vn.edu.uit.devorbit_api.service.SocialService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommunityMilestone3ContractTest {

    private ApplicationEventPublisher eventPublisher() {
        return mock(ApplicationEventPublisher.class);
    }

    private NotificationRepository notificationRepository() {
        return mock(NotificationRepository.class);
    }

    @Test
    void communityControllerExposesRestAndStompMappings() throws Exception {
        assertThat(StudentCommunityController.class.getAnnotation(RequestMapping.class).value())
                .containsExactly("/api/student/community");
        assertThat(StudentCommunityController.class.getMethod("getChannels").getAnnotation(GetMapping.class).value())
                .isEmpty();
        assertThat(StudentCommunityController.class
                .getMethod("getMessages", Long.class, int.class, int.class)
                .getAnnotation(GetMapping.class).value())
                .containsExactly("/channels/{channelId}/messages");
        assertThat(StudentCommunityController.class
                .getMethod("sendMessage", Long.class, ChatMessageRequest.class, java.security.Principal.class)
                .getAnnotation(MessageMapping.class).value())
                .containsExactly("/chat.send/{channelId}");
    }

    @Test
    void socialControllersExposeExpectedRoutes() throws Exception {
        assertThat(StudentSocialController.class.getAnnotation(RequestMapping.class).value())
                .containsExactly("/api/student");
        assertThat(StudentSocialController.class
                .getMethod("upsertRepoReview", String.class, Long.class, ReviewRequest.class)
                .getAnnotation(PostMapping.class).value())
                .containsExactly("/repos/{repoId}/review");
        assertThat(StudentSocialController.class
                .getMethod("voteRepo", String.class, Long.class, RepoVoteRequest.class)
                .getAnnotation(PostMapping.class).value())
                .containsExactly("/repos/{repoId}/vote");
        assertThat(PublicSocialController.class.getAnnotation(RequestMapping.class).value())
                .containsExactly("/api");
        assertThat(PublicSocialController.class
                .getMethod("getRepoSocialInfo", Long.class)
                .getAnnotation(GetMapping.class).value())
                .containsExactly("/repos/{repoId}/social-info");
        assertThat(PublicSocialController.class
                .getMethod("getCourseReviews", Long.class)
                .getAnnotation(GetMapping.class).value())
                .containsExactly("/courses/{courseId}/reviews");
    }

    @Test
    void syncChannelsCreatesGeneralCourseAndTechStackChannels() {
        ChatChannelRepository channelRepository = mock(ChatChannelRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        TechStackRepository techStackRepository = mock(TechStackRepository.class);
        CommunityMessageRepository messageRepository = mock(CommunityMessageRepository.class);
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        StudentUserRepository studentUserRepository = mock(StudentUserRepository.class);

        Course course = Course.builder().id(10L).maMH("SE104").tenMH("Nhap mon CNPM").build();
        TechStack techStack = TechStack.builder().id(7L).name("Spring Boot").build();
        when(channelRepository.findByChannelId(any())).thenReturn(Optional.empty());
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(techStackRepository.findAllDistinctOrderByName()).thenReturn(List.of(techStack));
        when(channelRepository.save(any(ChatChannel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CommunityChatService service = new CommunityChatService(
                eventPublisher(),
                notificationRepository(),
                channelRepository, messageRepository, studentUserRepository, courseRepository, techStackRepository);

        service.syncChannels();

        verify(channelRepository).save(argThat(channel -> channel.getChannelId().equals("general")
                && channel.getType() == ChatChannelType.GENERAL));
        verify(channelRepository).save(argThat(channel -> channel.getChannelId().equals("course-se104")
                && channel.getReferenceId().equals("10")));
        verify(channelRepository).save(argThat(channel -> channel.getChannelId().equals("tech-spring-boot")
                && channel.getType() == ChatChannelType.TECH_STACK));
    }

    @Test
    void sendMessagePersistsStudentMessageAndReturnsResponse() {
        ChatChannelRepository channelRepository = mock(ChatChannelRepository.class);
        CommunityMessageRepository messageRepository = mock(CommunityMessageRepository.class);
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        StudentUserRepository studentUserRepository = mock(StudentUserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        TechStackRepository techStackRepository = mock(TechStackRepository.class);

        ChatChannel channel = ChatChannel.builder().id(2L).channelId("general").name("Kênh chung").type(ChatChannelType.GENERAL).build();
        StudentUser student = StudentUser.builder().id(5L).studentCode("24520554").fullName("Nguyen Van A").email("a@gm.uit.edu.vn").build();
        when(channelRepository.findById(2L)).thenReturn(Optional.of(channel));
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(notificationRepository.findByIsReadFalseOrderByCreatedAtDesc()).thenReturn(List.of());
        when(messageRepository.save(any(CommunityMessage.class))).thenAnswer(invocation -> {
            CommunityMessage message = invocation.getArgument(0);
            message.setId(99L);
            return message;
        });

        CommunityChatService service = new CommunityChatService(
                eventPublisher(),
                notificationRepository(),
                channelRepository, messageRepository, studentUserRepository, courseRepository, techStackRepository);

        var response = service.sendMessage("24520554", 2L, new ChatMessageRequest("Xin chao"));

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.channelId()).isEqualTo(2L);
        assertThat(response.studentId()).isEqualTo(5L);
        assertThat(response.senderName()).isEqualTo("Nguyen Van A");
        assertThat(response.content()).isEqualTo("Xin chao");
    }

    @Test
    void messageMappingBroadcastsSavedMessageToChannelTopic() {
        CommunityChatService chatService = mock(CommunityChatService.class);
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        var response = new vn.edu.uit.devorbit_api.dto.community.ChatMessageResponse(
                9L, 2L, 5L, "Nguyen Van A", null, "Xin chao", null);
        when(chatService.sendMessage(eq("24520554"), eq(2L), any(ChatMessageRequest.class))).thenReturn(response);

        StudentCommunityController controller = new StudentCommunityController(chatService, messagingTemplate);
        controller.sendMessage(2L, new ChatMessageRequest("Xin chao"), () -> "24520554");

        verify(messagingTemplate).convertAndSend("/topic/channel/2", response);
    }

    @Test
    void socialServiceUpsertsRepoReviewAndCancelsVote() {
        RepoReviewRepository repoReviewRepository = mock(RepoReviewRepository.class);
        RepoVoteRepository repoVoteRepository = mock(RepoVoteRepository.class);
        CourseReviewRepository courseReviewRepository = mock(CourseReviewRepository.class);
        GithubRepoRepository githubRepoRepository = mock(GithubRepoRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        StudentUserRepository studentUserRepository = mock(StudentUserRepository.class);

        GithubRepo repo = GithubRepo.builder().id(3L).repoName("demo").githubUrl("https://github.com/demo/demo").build();
        StudentUser student = StudentUser.builder().id(5L).studentCode("24520554").fullName("Nguyen Van A").email("a@gm.uit.edu.vn").build();
        when(githubRepoRepository.findById(3L)).thenReturn(Optional.of(repo));
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student));
        when(repoReviewRepository.findByRepoIdAndStudentId(3L, 5L)).thenReturn(Optional.empty());
        when(repoReviewRepository.save(any(RepoReview.class))).thenAnswer(invocation -> invocation.getArgument(0));

SocialService service = new SocialService(eventPublisher(), repoReviewRepository, repoVoteRepository, courseReviewRepository,
                githubRepoRepository, courseRepository, studentUserRepository);

        var review = service.upsertRepoReview("24520554", 3L, new ReviewRequest(5, "Rat huu ich"));
        assertThat(review.rating()).isEqualTo(5);
        assertThat(review.comment()).isEqualTo("Rat huu ich");

        RepoVote existingVote = RepoVote.builder().id(6L).repo(repo).student(student).voteValue(1).build();
        when(repoVoteRepository.findByRepoIdAndStudentId(3L, 5L)).thenReturn(Optional.of(existingVote));
        var vote = service.voteRepo("24520554", 3L, new RepoVoteRequest(0));
        assertThat(vote.voteValue()).isEqualTo(0);
        verify(repoVoteRepository).delete(existingVote);
    }

    @Test
    void socialServiceReturnsPublicRepoAndCourseSummaries() {
        RepoReviewRepository repoReviewRepository = mock(RepoReviewRepository.class);
        RepoVoteRepository repoVoteRepository = mock(RepoVoteRepository.class);
        CourseReviewRepository courseReviewRepository = mock(CourseReviewRepository.class);
        GithubRepoRepository githubRepoRepository = mock(GithubRepoRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        StudentUserRepository studentUserRepository = mock(StudentUserRepository.class);

        GithubRepo repo = GithubRepo.builder().id(3L).repoName("demo").githubUrl("https://github.com/demo/demo").build();
        Course course = Course.builder().id(4L).maMH("SE104").tenMH("Nhap mon CNPM").build();
        StudentUser student = StudentUser.builder().id(5L).studentCode("24520554").fullName("Nguyen Van A").email("a@gm.uit.edu.vn").build();
        when(githubRepoRepository.findById(3L)).thenReturn(Optional.of(repo));
        when(courseRepository.findById(4L)).thenReturn(Optional.of(course));
        when(repoVoteRepository.sumVoteValueByRepoId(3L)).thenReturn(2);
        when(repoReviewRepository.averageRatingByRepoId(3L)).thenReturn(4.5);
        when(repoReviewRepository.findByRepoIdOrderByUpdatedAtDesc(3L)).thenReturn(List.of(
                RepoReview.builder().id(11L).repo(repo).student(student).rating(5).comment("Tot").build()));
        when(courseReviewRepository.averageRatingByCourseId(4L)).thenReturn(4.0);
        when(courseReviewRepository.findByCourseIdOrderByUpdatedAtDesc(4L)).thenReturn(List.of(
                CourseReview.builder().id(12L).course(course).student(student).rating(4).comment("On").build()));

SocialService service = new SocialService(eventPublisher(), repoReviewRepository, repoVoteRepository, courseReviewRepository,
                githubRepoRepository, courseRepository, studentUserRepository);

        assertThat(service.getRepoSocialInfo(3L).voteScore()).isEqualTo(2);
        assertThat(service.getRepoSocialInfo(3L).averageRating()).isEqualTo(4.5);
        assertThat(service.getCourseReviews(4L).averageRating()).isEqualTo(4.0);
    }

    @Test
    void messageHistoryUsesPagination() {
        ChatChannelRepository channelRepository = mock(ChatChannelRepository.class);
        CommunityMessageRepository messageRepository = mock(CommunityMessageRepository.class);
        NotificationRepository notificationRepository = mock(NotificationRepository.class);
        StudentUserRepository studentUserRepository = mock(StudentUserRepository.class);
        CourseRepository courseRepository = mock(CourseRepository.class);
        TechStackRepository techStackRepository = mock(TechStackRepository.class);
        ChatChannel channel = ChatChannel.builder().id(2L).channelId("general").name("Kênh chung").type(ChatChannelType.GENERAL).build();
        StudentUser student = StudentUser.builder().id(5L).studentCode("24520554").fullName("Nguyen Van A").email("a@gm.uit.edu.vn").build();
        CommunityMessage message = CommunityMessage.builder().id(1L).channel(channel).student(student).content("Xin chao").build();
        when(channelRepository.existsById(2L)).thenReturn(true);
        when(messageRepository.findByChannelIdAndDeletedFalseOrderByCreatedAtDesc(2L, PageRequest.of(0, 50)))
                .thenReturn(new PageImpl<>(List.of(message)));

        CommunityChatService service = new CommunityChatService(
                eventPublisher(),
                notificationRepository(),
                channelRepository, messageRepository, studentUserRepository, courseRepository, techStackRepository);

        assertThat(service.getMessages(2L, 0, 50).getContent()).hasSize(1);
    }
}
