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

@Slf4j
@RequiredArgsConstructor
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    public final static String SAVE_DLQ_KEY = "ChatMessageDLQ";
    public final static String UPDATE_DLQ_KEY = "ChatRoomUpdateDLQ";
    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {

        log.warn("비동기 예외 발생 exception {} method {} params {}", ex, method, params);
        if (ex instanceof ChatException chatex) {
            if (!ChatExceptionCode.MESSAGE_SENDING_FAILED.code().equals(chatex.getCode().code())) {
                log.warn("[AsyncExceptionHandler]: C4020 이외는 DLQ 제외");
                return;
            }
        }
        for (Object param : params) {
            if(param instanceof ChatMessage){
                log.warn("sender:{}, roomId:{}, time:{}",((ChatMessage) param).getSender(),
                        ((ChatMessage) param).getChatRoomId(),
                        ((ChatMessage) param).getSentAt());
                try {
                    if("saveChatMessage".equals(method.getName())) {
                        //천개 이하라면 저장
                        Long s_size = redisTemplate.opsForList().size(SAVE_DLQ_KEY);
                        if(s_size == null || s_size < 1000){
                            redisTemplate.opsForList().leftPush(SAVE_DLQ_KEY, (ChatMessage) param);
                            redisTemplate.expire(SAVE_DLQ_KEY, Duration.ofDays(4));             //4일의 시간동안 DLQ 생존
                            log.warn("Fail ChatMessage Save and Save Redis in ChatMessageDLQ");
                        }
                        else {
                            //이상이라면 그냥 흘려보냄
                            log.warn("ChatMessageDLQ limit exceeded: To avoid overload, dropped messages exceeding the DLQ limit");
                        }
                    }
                    if("updateChatRoomInfo".equals(method.getName())){
                        Long u_size = redisTemplate.opsForList().size(UPDATE_DLQ_KEY);
                        if(u_size ==null || u_size < 1000){
                            redisTemplate.opsForList().leftPush(UPDATE_DLQ_KEY, (ChatMessage) param);
                            redisTemplate.expire(UPDATE_DLQ_KEY, Duration.ofDays(4));
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
