package smu.capstone.domain.chatroom.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import smu.capstone.common.response.ChatRoomResponse;

@Slf4j
@RestControllerAdvice
public class ChatRoomExceptionHandler {

    @ExceptionHandler(ChatRoomException.class)
    public ResponseEntity<ChatRoomResponse<String>> handle(ChatRoomException e) {
        return ChatRoomResponse.fail(e.getStatusCode());
    }
}
