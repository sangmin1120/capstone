package smu.capstone.intrastructure.chatting.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.domain.chatroom.exception.ChatRoomException;
import smu.capstone.intrastructure.chatting.util.RedisSessionManager;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

    private final RedisSessionManager redisSessionManager;

    @EventListener
    public void handleSessionDisconnect(final SessionDisconnectEvent event) {
        log.info("Session disconnected");
        deleteSessionInfo(event.getSessionId());
    }

    //Disconnect 시 header 정보 X -> 세션 Attribute 못 가져옴.
    public void deleteSessionInfo(String sessionId) {
        try {
            redisSessionManager.removeChatUserSession(sessionId);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ChatRoomException(CommonStatusCode.INTERNAL_SERVER_ERROR);
        }
        log.info("Session info delete");
    }
}
