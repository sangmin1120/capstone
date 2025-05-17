package smu.capstone.common.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FcmExceptionCode implements StatusCode{

    INVALID_REQUEST_MESSAGE(HttpStatus.UNAUTHORIZED, "F401-1", "요청 메시지가 없습니다."),
    INVALID_REQUEST_URI(HttpStatus.NOT_FOUND, "F401-2", "서버 에러? 요청 주소가 없습니다."),
    INVALID_REQUEST_PATTERN(HttpStatus.NOT_FOUND,"F402-3", "JsonProcessingException"),
    FCM_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "F501-1", "서버 에러"),
    FCM_TOKEN_MISSING(HttpStatus.BAD_REQUEST, "F401-4", "fcmToken missing");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
