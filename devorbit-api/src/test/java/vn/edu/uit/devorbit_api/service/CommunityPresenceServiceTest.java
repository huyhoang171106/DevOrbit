package vn.edu.uit.devorbit_api.service;

import org.junit.jupiter.api.Test;
import vn.edu.uit.devorbit_api.dto.community.ChannelPresenceResponse;
import vn.edu.uit.devorbit_api.entity.StudentUser;
import vn.edu.uit.devorbit_api.repository.StudentUserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CommunityPresenceServiceTest {

    private final StudentUserRepository studentUserRepository = mock(StudentUserRepository.class);
    private final CommunityPresenceService service = new CommunityPresenceService(studentUserRepository);

    @Test
    void subscribeAddsStudentToChannelPresence() {
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student("24520554", "Bao Nguyen")));

        ChannelPresenceResponse presence = service.subscribe("session-1", "sub-1", 7L, "24520554");

        assertThat(presence.channelId()).isEqualTo(7L);
        assertThat(presence.members()).singleElement().satisfies(member -> {
            assertThat(member.studentId()).isEqualTo(42L);
            assertThat(member.studentCode()).isEqualTo("24520554");
            assertThat(member.displayName()).isEqualTo("Bao Nguyen");
        });
    }

    @Test
    void duplicateStudentSessionsAppearOncePerChannel() {
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student("24520554", "Bao Nguyen")));

        service.subscribe("session-1", "sub-1", 7L, "24520554");
        ChannelPresenceResponse presence = service.subscribe("session-2", "sub-2", 7L, "24520554");

        assertThat(presence.members()).hasSize(1);
    }

    @Test
    void unsubscribeRemovesOnlyMatchingSubscription() {
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student("24520554", "Bao Nguyen")));
        when(studentUserRepository.findByStudentCode("24520001")).thenReturn(Optional.of(student(99L, "24520001", "An Tran")));
        service.subscribe("session-1", "sub-1", 7L, "24520554");
        service.subscribe("session-2", "sub-2", 7L, "24520001");

        ChannelPresenceResponse presence = service.unsubscribe("session-1", "sub-1");

        assertThat(presence).isNotNull();
        assertThat(presence.channelId()).isEqualTo(7L);
        assertThat(presence.members())
                .extracting(member -> member.studentCode())
                .containsExactly("24520001");
    }

    @Test
    void disconnectRemovesAllSubscriptionsForSession() {
        when(studentUserRepository.findByStudentCode("24520554")).thenReturn(Optional.of(student("24520554", "Bao Nguyen")));
        service.subscribe("session-1", "sub-1", 7L, "24520554");
        service.subscribe("session-1", "sub-2", 8L, "24520554");

        var affectedChannels = service.disconnect("session-1");

        assertThat(affectedChannels).containsExactlyInAnyOrder(7L, 8L);
        assertThat(service.presenceForChannel(7L).members()).isEmpty();
        assertThat(service.presenceForChannel(8L).members()).isEmpty();
    }

    private static StudentUser student(String studentCode, String fullName) {
        return student(42L, studentCode, fullName);
    }

    private static StudentUser student(Long id, String studentCode, String fullName) {
        return StudentUser.builder()
                .id(id)
                .studentCode(studentCode)
                .fullName(fullName)
                .email(studentCode + "@gm.uit.edu.vn")
                .passwordHash("hash")
                .build();
    }
}
