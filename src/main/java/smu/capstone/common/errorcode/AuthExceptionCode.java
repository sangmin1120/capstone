package smu.capstone.common.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthExceptionCode implements StatusCode {
    AUTHORIZATION_REQUIRED(HttpStatus.UNAUTHORIZED, "A401-1", "인증이 필요합니다."),
    INVALID_ID_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "A401-2", "아이디 혹은 비밀번호가 잘못되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A401-3", "올바르지 않은 토큰입니다."),
    REFRESH_TOKEN_NOT_EXIST(HttpStatus.UNAUTHORIZED, "A401-4", "리프레쉬 토큰이 유효하지 않습니다."),
    NOT_VERIFIED_MAIL(HttpStatus.UNAUTHORIZED, "A401-5", "인증을 진행해주세요"),
    INVALID_VERIFICATION_KEY(HttpStatus.UNAUTHORIZED, "A401-6", "잘못된 인증번호입니다."),

    EXPIRED_TOKEN(HttpStatus.FORBIDDEN, "A403-1", "토큰의 유효 시간이 만료 되었습니다."),

    DUPLICATED_ID(HttpStatus.CONFLICT, "A409-1", "이미 존재하는 아이디입니다."),
    NOT_EQUAL_PASSWORD(HttpStatus.CONFLICT, "A409-2", "비밀번호 확인이 다릅니다."),
    DUPLICATED_MAIL(HttpStatus.CONFLICT, "T409-1", "이미 동일 메일로 가입된 계정이 있습니다."),

    FAIL_TO_SEND_MAIL(HttpStatus.INTERNAL_SERVER_ERROR, "A500-1", "메일 전송에 실패하였습니다."),
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
