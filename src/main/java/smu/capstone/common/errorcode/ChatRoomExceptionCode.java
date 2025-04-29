package smu.capstone.common.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ChatRoomExceptionCode implements StatusCode{
    USER_DEACTIVATED(HttpStatus.OK, "CR200-1", "상대 회원이 탈퇴하여 채팅이 불가능합니다."),
    NOT_FOUND_ROOM(HttpStatus.NOT_FOUND, "CR404-1", "채팅방의 ID가 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "CR404-2", "상대 회원의 ID가 존재하지 않습니다."),
    NOT_FOUND_ALL(HttpStatus.NOT_FOUND, "CR404-3", "채팅방 혹은 상대 회원의 ID가 존재하지 않습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

}
