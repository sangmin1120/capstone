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
import org.springframework.stereotype.Component;
import smu.capstone.common.errorcode.ChatRoomExceptionCode;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.chatroom.domain.ChatRoomUser;
import smu.capstone.domain.chatroom.exception.ChatRoomException;
import smu.capstone.domain.chatroom.repository.ChatRoomUserRepository;
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

    private final ChatRoomUserRepository chatRoomUserRepository;
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
                throw new RestApiException(CommonStatusCode.INVALID_PARAMETER);
            }

            String accessToken = authHeader.substring("Bearer ".length());
            tokenProvider.validateToken(TokenType.ACCESS_TOKEN, accessToken);

            //Principal 등록
            Authentication auth = new UsernamePasswordAuthenticationToken(tokenService.getUserid(accessToken),
                        null, List.of(new SimpleGrantedAuthority(Authority.ROLE_USER.toString())));
            //SecurityContextHolder.getContext().setAuthentication(auth);
            accessor.setUser(auth);

            String username = accessor.getUser().getName();

            //상대 유저가 탈퇴한 사용자일 경우 채팅창 비활성화
            ChatRoomUser user = chatRoomUserRepository.findByChatRoom_IdAndUserEntity_AccountId(roomId, username).orElseThrow(
                    () -> new ChatRoomException(ChatRoomExceptionCode.NOT_FOUND_ROOM)
            );
            if(user.isOpponentDeleted()){
                throw new ChatRoomException(ChatRoomExceptionCode.USER_DEACTIVATED);
            }

            //Inactive거나 AVAIL일 경우 채팅창 비활성화 - 논리에 맞지 않은 접근 방지
            if(!chatRoomUserRepository.existsByChatRoom_IdAndUserEntity_accountIdAndActivation(roomId, username, ChatRoomUser.Activation.ACTIVE)){
                throw new RestApiException(CommonStatusCode.FORBIDDEN);
            }

            //이미 존재하는 경우 기존 세션 종료
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
//        else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
//            String roomId = extractRoomIdFromDestination(accessor.getDestination());
//            //맞는지 검증
//            if(roomId != null) {
//                if(!chatRoomUserRepository.existsByChatRoom_IdAndUserEntity_accountId(roomId,accessor.getUser().getName())){
//                    throw new RestApiException(CommonStatusCode.FORBIDDEN);
//                }
//            }
//        }
        return message;
    }
    private String extractRoomIdFromDestination(String destination) {
        if(destination != null) {
            String[] parts = destination.split("/");
            //"chat"이면
            if(parts.length == 4 && "chat".equals(parts[2])) {
                //뒤에 있는 roomId 반환
                log.info("roomId: {}", parts[3]);
                return parts[3];
            }
        }
        return null;
    }
}
