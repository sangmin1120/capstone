package smu.capstone.domain.chatroom.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import smu.capstone.common.errorcode.StatusCode;

@Slf4j
@Getter
public class ChatRoomException extends RuntimeException {

    private final StatusCode statusCode;

    public ChatRoomException(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}
