package smu.capstone.domain.chat.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import smu.capstone.common.errorcode.ChatExceptionCode;

import java.security.Principal;

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
        try {
            if(principal == null || principal.getName() == null) {
                throw new IllegalArgumentException();
            }
            messagingTemplate.convertAndSendToUser(principal.getName(),"/queue/error", e.getCode().createErrorMessage());
        }catch (MessageDeliveryException ex) {
            log.error("message delivery exception: error code is {}", e.getMessage());
        }
    }


    @MessageExceptionHandler(MessageConversionException.class)
    public void handleConversionError(Principal principal ,MessageConversionException e) {
        try {
            if(principal == null || principal.getName() == null) {
                log.info("principal is null");
                throw new IllegalArgumentException();
            }
            messagingTemplate.convertAndSendToUser(principal.getName(),
                    "/queue/error",
                    ChatExceptionCode.MESSAGE_SENDING_FAILED.createErrorMessage()
            );
        }catch (MessageDeliveryException ex) {
            log.error("message delivery exception: error code is {}", e.getMessage());
        }
    }
}
