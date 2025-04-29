package smu.capstone.intrastructure.chatting.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.errorcode.StatusCode;
import smu.capstone.common.exception.RestApiException;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;

@Slf4j
@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {


    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
        if(ex instanceof MessageDeliveryException){
            Throwable cause = ex.getCause();
            if(cause instanceof AccessDeniedException) return sendErrorMessage(cause, CommonStatusCode.UNAUTHORIZED);
            else if(cause instanceof RestApiException) {
                return sendErrorMessage(cause, ((RestApiException) cause).getStatusCode());
            }
        }
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    private Message<byte[]> sendErrorMessage(Throwable cause, StatusCode code) {
        //헤더 설정
        final StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(cause.getMessage());

        //Payload 커스텀 - 임시
        String payload = code.code() + ": " + code.message();
        log.error("throw message code :: "+ code.code() + code.message());
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(payload.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
    }
}
