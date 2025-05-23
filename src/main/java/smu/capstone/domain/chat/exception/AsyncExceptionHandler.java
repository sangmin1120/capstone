package smu.capstone.domain.chat.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.data.redis.core.RedisTemplate;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.warn("비동기 예외 발생 exception {} method {} params {}", ex, method, params);
        for (Object param : params) {
            if(param instanceof ChatMessage){
                log.warn("sender:{}, roomId:{}, time:{}",((ChatMessage) param).getSender(),
                        ((ChatMessage) param).getChatRoomId(),
                        ((ChatMessage) param).getSentAt());
                try {
                    if("saveChatMessage".equals(method.getName())) {
                        redisTemplate.opsForList().leftPush("ChatMessageDLQ", (ChatMessage) param);
                        log.warn("Fail ChatMessage Save and Save Redis in DLQ");
                    }
                    if("updateChatRoomInfo".equals(method.getName())){
                        redisTemplate.opsForList().leftPush("ChatRoomUpdateDLQ", (ChatMessage) param);
                        log.warn("Fail ChatMessage Update and Update Redis in ChatRoomUpdateDLQ");
                    }
                }catch (Exception e){
                    log.error("Fail Message save(final):\n sender:{}, roomId:{}, time:{}",
                            ((ChatMessage) param).getSender(),
                            ((ChatMessage) param).getChatRoomId(),
                            ((ChatMessage) param).getSentAt());
                }
            }
        }
        //TO DO: 재시도 로직 필요, 마스킹 처리.
    }
}
