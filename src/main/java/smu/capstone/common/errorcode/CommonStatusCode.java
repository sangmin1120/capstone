package smu.capstone.common.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CommonStatusCode implements StatusCode{
    //200
    OK(HttpStatus.OK, "C200", "요청이 성공하였습니다."),
    CREATED(HttpStatus.CREATED, "C201", "리소스가 생성되었습니다."),

    //400
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "C400","올바르지 않은 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C401", "사용자 인증에 실패하였습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C403", "권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "C404", "리소스가 존재하지 않습니다."),

    NOT_FOUND_BOARD_TYPE(HttpStatus.NOT_FOUND,"C404-1","BoardType 존재하지 않습니다."),
    NOT_FOUND_BOARD_ID(HttpStatus.NOT_FOUND,"C404-2","게시글이 존재하지 않습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "C404-3","사용자가 존재하지 않습니다."),
    NOT_FOUND_EXERCISE_DIARY(HttpStatus.NOT_FOUND, "C404-4","운동 기록을 찾을 수 없습니다."),

    VALIDATION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "C422-1", "유효성 검증에 실패하였습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C500-1", "서버 내부 에러입니다."),
    ;

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
