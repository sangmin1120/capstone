package smu.capstone.intrastructure.chatting.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.errorcode.StatusCode;
import smu.capstone.common.exception.RestApiException;

import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

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
        accessor.setContentType(MimeTypeUtils.APPLICATION_JSON);

        //Payload 커스텀
        byte[] payload = getErrorPayload(code);

        log.error("throw message code ::{} {} ", code.code(), code.message());
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
    }

    private Map<String, Object> getErrorMessage(StatusCode code) {
        Map<String, Object> errorPayload = new HashMap<>();
        errorPayload.put("code", code.code());
        errorPayload.put("message", code.message());
        return errorPayload;
    }

    private byte[] getErrorPayload(StatusCode code) {
        Map<String, Object> errorMessage = getErrorMessage(code);
        try{
            return objectMapper.writeValueAsBytes(errorMessage);
        }catch (JsonProcessingException e){
            log.error("Json 직렬화 오류", e);
            return ("{\"code\":\"INTERNAL_ERROR\"," +
                    "\"message\":\"에러 응답 생성 실패\"}").getBytes(StandardCharsets.UTF_8);
        }
    }
}
