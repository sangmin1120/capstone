package smu.capstone.intrastructure.chatting.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import smu.capstone.domain.member.entity.Authority;
import smu.capstone.intrastructure.chatting.util.RedisSessionManager;
import smu.capstone.intrastructure.chatting.util.SessionManager;
import smu.capstone.intrastructure.jwt.TokenType;
import smu.capstone.intrastructure.jwt.service.TokenProvider;
import smu.capstone.intrastructure.jwt.service.TokenService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Component
public class StompHandler implements ChannelInterceptor {


    private final RedisSessionManager redisSessionManager;
    private final SessionManager sessionManager;
    private final TokenProvider tokenProvider;
    private final TokenService tokenService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
                .getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            String roomId = accessor.getFirstNativeHeader("RoomId");

            if (authHeader == null || roomId == null || roomId.isEmpty()) {
                log.error("Authorization header or roomId is null");
                throw new IllegalArgumentException("Authorization header or roomId is null");
            }

            String accessToken = authHeader.substring("Bearer ".length());
            tokenProvider.validateToken(TokenType.ACCESS_TOKEN, accessToken);

            //Principal 등록
            Authentication auth = new UsernamePasswordAuthenticationToken(tokenService.getUserid(accessToken),
                        null, List.of(new SimpleGrantedAuthority(Authority.ROLE_USER.toString())));
            SecurityContextHolder.getContext().setAuthentication(auth);
            accessor.setUser(auth);

            //이미 존재하는 경우 기존 세션 종료
            String username = accessor.getUser().getName();
            if(redisSessionManager.hasUserSession(roomId, username)) {
                String sessionId = redisSessionManager.getHashValue(roomId, username);
                redisSessionManager.removeSessionKey(sessionId);
                try {
                    sessionManager.closeSession(sessionId);
                }catch (Exception e) {
                    e.printStackTrace();
                    log.error("세션 오류");
                }
            }
            //세션속성으로 설정
            log.info("redis set hash in session");
            redisSessionManager.putChatUserSession(roomId, username, accessor.getSessionId());
            log.info("현재 세션 {}", accessor.getSessionId());
        }
        return message;
    }
}
