package smu.capstone.intrastructure.chatting.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/***
 * roomId:userId -> sessionId 저장해 갱신
 * "UserSession":sessionId -> roomId:userId 저장해 정보 꺼냄
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RedisSessionManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String REDIS_SESSION_KEY = "UserSession";

    /*채팅룸 사용자의 정보를 Redis Hash에 저장하는 메서드*/
    public void putChatUserSession(String roomId, String username, String sessionId){
        redisTemplate.opsForHash().put(roomId, username, sessionId);
        redisTemplate.opsForHash().put(REDIS_SESSION_KEY, sessionId, roomId+":"+username);
        //1일 TTL 설정
        //redisTemplate.expire(roomId, Duration.ofDays(1));
    }

    public String getHashValue(String key, String hashKey){
        return (String) redisTemplate.opsForHash().get(key, hashKey);
    }

    /* 해당 채팅방에 실시간으로 남아있는 사용자가 몇 명인지 확인하는 메서드 */
    public Long getChatUserSessionCount(String roomId){
        return redisTemplate.opsForHash().size(roomId);
    }

    public void removeSessionKey(String sessionId){
        redisTemplate.opsForHash().delete(REDIS_SESSION_KEY, sessionId);
    }

    public void removeChatUserSession(String sessionId){
        Map<String, String> roomUserToken = getRoomUserInfoBySessionId(sessionId);

        String roomId = roomUserToken.get("roomId");
        String username = roomUserToken.get("username");

        redisTemplate.opsForHash().delete(roomId, username);
        this.removeSessionKey(sessionId);

        //room에 남아있는 사람이 없을 경우 명시적으로 완전 삭제
        if (redisTemplate.opsForHash().size(roomId) == 0) {
            redisTemplate.delete(roomId);
        }
    }


    /* 이미 접속해있는지 확인하는 메서드. 있다면 true, 없다면 false를 반환*/
    public boolean hasUserSession(String roomId, String username){
        return redisTemplate.opsForHash().hasKey(roomId, username);
    }

    /* 상대가 접속 중인지 확인하는 메서드 */
    public boolean isAloneInRoom(String roomId){
        return (redisTemplate.opsForHash().size(roomId) == 1);
    }

    /**SessionId로 roomId와 UserId 정보를 얻는 메서드**/
    public Map<String, String> getRoomUserInfoBySessionId(String sessionId){
        Map<String, String> roomUserInfo = new HashMap<>();
        String roomUserToken = this.getHashValue(REDIS_SESSION_KEY, sessionId);
        if(roomUserToken == null)
            return null;
        String[] tokens = roomUserToken.split(":", 2);

        roomUserInfo.put("roomId", tokens[0]);
        roomUserInfo.put("username", tokens[1]);

        return roomUserInfo;
    }
}
