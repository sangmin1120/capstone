package smu.capstone.intrastructure.chatting.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public class SessionManager {

    private final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    public WebSocketSession getSession(String sessionId){
        return sessionMap.get(sessionId);
    }

    /* HashMap에서 session을 삭제하는 메서드 */
    public void removeSession(String sessionId){
        sessionMap.remove(sessionId);
    }

    public void putSession(String sessionId, WebSocketSession session){
        sessionMap.put(sessionId, session);
    }

    public void closeSession(String sessionId) throws IOException {
        WebSocketSession session = sessionMap.get(sessionId);

        if(session != null){
            log.info("session close: id:{}", sessionId);
            session.close();
        }
        removeSession(sessionId);
    }
}
