package smu.capstone.common.exception;

import smu.capstone.common.errorcode.StatusCode;

public class AppException extends RuntimeException {
    private final StatusCode statusCode;

    public AppException(StatusCode statusCode) {
        super(statusCode.message()); // Optional: 에러 메시지
        this.statusCode = statusCode;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }
}

