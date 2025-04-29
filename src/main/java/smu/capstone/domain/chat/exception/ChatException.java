package smu.capstone.domain.chat.exception;

import lombok.Getter;
import smu.capstone.common.errorcode.ChatExceptionCode;

@Getter
public class ChatException extends RuntimeException{

    private final ChatExceptionCode code;

    public ChatException(ChatExceptionCode code) {
        super(code.message());
        this.code = code;
    }
}
