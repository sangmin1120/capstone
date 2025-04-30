package smu.capstone.intrastructure.chatting.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.domain.chat.service.ChatLeavePublisher;
import smu.capstone.domain.chatroom.exception.ChatRoomException;
import smu.capstone.intrastructure.chatting.util.RedisSessionManager;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebSocketEventListener {

    private final RedisSessionManager redisSessionManager;
    private final ChatLeavePublisher chatLeavePublisher;

    @EventListener
    public void handleSessionDisconnect(final SessionDisconnectEvent event) {
        log.info("Session disconnected");

        //읽음 처리
        Map<String, String> roomUserToken = redisSessionManager.getRoomUserInfoBySessionId(event.getSessionId());
        if(roomUserToken == null) {
            log.error("Session disconnect failed, token is error");
            return;
        }
        chatLeavePublisher.sendLeaveState(roomUserToken.get("roomId"),
                roomUserToken.get("username"));
        log.info("채팅방 나갔다는 메시지 보냄");
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
