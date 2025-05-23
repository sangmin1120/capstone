package smu.capstone.common.errorcode;

import java.util.HashMap;
import java.util.Map;

public enum ChatExceptionCode {

    ACCESS_DENIED("C1201","ACCESS_DENIED"),

    //채팅 메시지 Error - 커스텀 코드 필요
    NULL_CONTENT("C4000","메시지가 비어있습니다."),
    ROOM_NOT_EQUAL("C4000","메시지에 적힌 채팅방과 현재 채팅방이 일치하지 않습니다."),
    ROOM_NOT_EXIST("C4000","채팅방이 존재하지 않습니다."),
    NO_ONE_PRESENT("C4000", "채팅방에 참가하는 사람이 아무도 없습니다."),
    USER_NOT_FOUND("C4000", "사용자를 찾을 수 없습니다."),

    INVALID_MESSAGE_TYPE("C4010","유효하지 않은 메시지 타입입니다."),
    MESSAGE_SENDING_FAILED("C4020","메시지 전송에 실패하였습니다."),
    MESSAGE_SAVE_FAILED("C4020", "메시지 저장에 실패했습니다."),
    DATA_BIND_ERROR("C4020","메시지 포맷 처리에 문제가 생겼습니다."),
    CHATROOM_STATE_UPDATE_ERROR("C4021", "메시지 전송으로 인한 채팅방 상태 업데이트에 실패했습니다."),

    IO_ERROR("C4030","메시지 처리과정에서 입출력 오류가 발생하였습니다."),
    INTERNAL_SERVER_ERROR("C4040", "서버 내부 오류");

    private final String code;
    private final String message;

    ChatExceptionCode(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String message(){
        return message;
    }

    public String code(){
        return code;
    }

    public Map<String, String> createErrorMessage(){
        Map<String, String> message = new HashMap<>();
        message.put("code", code());
        message.put("message", message());
        return message;
    }
}
