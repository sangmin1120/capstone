package smu.capstone.domain.chat.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import smu.capstone.common.errorcode.ChatExceptionCode;

import java.security.Principal;
import java.util.Map;

/***
 * 해당 부분 오류 나는지 확인 필요
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class ChatExceptionHandler {

    private final SimpMessagingTemplate messagingTemplate;

    ///client sub url: user/{username}/queue/error
    @MessageExceptionHandler(ChatException.class)
    public void handleMessageException(Principal principal, ChatException e) {
        sendErrorMessage(principal, e, e.getCode().createErrorMessage());
    }


    @MessageExceptionHandler(MessageConversionException.class)
    public void handleConversionError(Principal principal ,MessageConversionException e) {
        sendErrorMessage(principal, e,
                ChatExceptionCode.MESSAGE_SENDING_FAILED.createErrorMessage());

    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValid(Principal principal, MethodArgumentNotValidException e) {
        sendErrorMessage(principal, e, ChatExceptionCode.DATA_BIND_ERROR.createErrorMessage());
    }

    private void sendErrorMessage(Principal principal, Exception e, Map<String, String> errorMessage) {
        try {
            if(principal == null || principal.getName() == null) {
                log.info("principal is null");
                throw new IllegalArgumentException();
            }
            messagingTemplate.convertAndSendToUser(principal.getName(),
                    "/queue/error",
                    errorMessage
            );
        }catch (MessageDeliveryException ex) {
            log.error("message delivery exception: error code is {}", e.getMessage());
        }
    }
}
