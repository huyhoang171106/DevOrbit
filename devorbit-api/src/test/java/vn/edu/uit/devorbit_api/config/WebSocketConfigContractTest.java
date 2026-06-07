package vn.edu.uit.devorbit_api.config;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import vn.edu.uit.devorbit_api.service.JwtService;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebSocketConfigContractTest {

    @Test
    void configEnablesStompMessageBroker() {
        assertThat(WebSocketConfig.class.getAnnotation(EnableWebSocketMessageBroker.class)).isNotNull();
        assertThat(WebSocketMessageBrokerConfigurer.class).isAssignableFrom(WebSocketConfig.class);
    }

    @Test
    void connectInterceptorAuthenticatesStudentJwtAsPrincipal() {
        JwtService jwtService = mock(JwtService.class);
        when(jwtService.isTokenValid("student-token")).thenReturn(true);
        when(jwtService.extractTokenType("student-token")).thenReturn("STUDENT");
        when(jwtService.extractUsername("student-token")).thenReturn("24520554");

        ChannelInterceptor interceptor = new WebSocketConfig(jwtService).communityJwtChannelInterceptor();
        Message<?> result = interceptor.preSend(connectMessage("Bearer student-token"), mock(MessageChannel.class));

        Object user = StompHeaderAccessor.wrap(result).getUser();
        assertThat(user).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        UsernamePasswordAuthenticationToken principal = (UsernamePasswordAuthenticationToken) user;
        assertThat(principal.getName()).isEqualTo("24520554");
        assertThat(principal.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_STUDENT");
    }

    @Test
    void connectInterceptorRejectsMissingInvalidOrNonStudentJwt() {
        JwtService jwtService = mock(JwtService.class);
        when(jwtService.isTokenValid("admin-token")).thenReturn(true);
        when(jwtService.extractTokenType("admin-token")).thenReturn("ADMIN");
        ChannelInterceptor interceptor = new WebSocketConfig(jwtService).communityJwtChannelInterceptor();

        assertThatThrownBy(() -> interceptor.preSend(connectMessage(null), mock(MessageChannel.class)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> interceptor.preSend(connectMessage("Bearer invalid-token"), mock(MessageChannel.class)))
                .isInstanceOf(AccessDeniedException.class);
        assertThatThrownBy(() -> interceptor.preSend(connectMessage("Bearer admin-token"), mock(MessageChannel.class)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void securityConfigPermitsCommunityWebSocketHandshakePath() throws Exception {
        String securityConfig = Files.readString(Path.of("src/main/java/vn/edu/uit/devorbit_api/config/SecurityConfig.java"));

        assertThat(securityConfig).contains(".requestMatchers(\"/ws/community/**\").permitAll()");
    }

    private static Message<byte[]> connectMessage(String authorizationHeader) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.CONNECT);
        if (authorizationHeader != null) {
            accessor.setNativeHeader("Authorization", authorizationHeader);
        }
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }
}
