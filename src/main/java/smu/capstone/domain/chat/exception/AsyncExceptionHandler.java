package smu.capstone.domain.chat.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import smu.capstone.common.errorcode.ChatExceptionCode;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.lang.reflect.Method;
import java.time.Duration;

//TODO: 비동기 에러 catch해 Retry 로직 시도 필요, 필요할 경우 마스킹 처리
@Slf4j
@RequiredArgsConstructor
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

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
                        //천개 이하라면 저장
                        if(redisTemplate.opsForList().size("ChatMessageDLQ") < 1000){
                            redisTemplate.opsForList().leftPush("ChatMessageDLQ", (ChatMessage) param);
                            redisTemplate.expire("ChatMessageDLQ", Duration.ofDays(4));             //4일의 시간동안 DLQ 생존
                            log.warn("Fail ChatMessage Save and Save Redis in ChatMessageDLQ");
                        }
                        else {
                            //이상이라면 그냥 흘려보냄
                            log.warn("ChatMessageDLQ limit exceeded: To avoid overload, dropped messages exceeding the DLQ limit");
                        }
                    }
                    //이경우 수동 업데이트보다는 재시도 로직가 더 필요
                    if("updateChatRoomInfo".equals(method.getName())){
                        if(redisTemplate.opsForList().size("ChatRoomUpdateDLQ") < 1000){
                            redisTemplate.opsForList().leftPush("ChatRoomUpdateDLQ", (ChatMessage) param);
                            redisTemplate.expire("ChatRoomUpdateDLQ", Duration.ofDays(4));
                            log.warn("Fail ChatMessage Update and Update Redis in ChatRoomUpdateDLQ");
                        }
                        else {
                            log.warn("ChatRoomUpdateDLQ limit exceeded: To avoid overload, dropped messages exceeding the DLQ limit");
                        }
                        //채팅방 비활성화와 관련있으므로 메시지 알림 전송.
                        messagingTemplate.convertAndSendToUser(((ChatMessage) param).getSender(),
                                "/queue/error",
                                ChatExceptionCode.CHATROOM_STATE_UPDATE_ERROR.createErrorMessage());
                    }
                }catch (Exception e){
                    log.error("Fail Message save(final):\n sender:{}, roomId:{}, time:{}",
                            ((ChatMessage) param).getSender(),
                            ((ChatMessage) param).getChatRoomId(),
                            ((ChatMessage) param).getSentAt());
                }
            }
        }
    }
}
