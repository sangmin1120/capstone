package smu.capstone.domain.chat.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.lang.reflect.Method;

@Slf4j
@RequiredArgsConstructor
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.warn("비동기 예외 발생 exception {} method {} params {}", ex, method, params);
        for (Object param : params) {
            if(param instanceof ChatMessage){
                log.warn("sender:{}, roomId:{}, time:{}",((ChatMessage) param).getSender(),
                        ((ChatMessage) param).getChatRoomId(),
                        ((ChatMessage) param).getSentAt());
            }
        }
        //TO DO: 재시도 로직 필요, 마스킹 처리.
    }
}
