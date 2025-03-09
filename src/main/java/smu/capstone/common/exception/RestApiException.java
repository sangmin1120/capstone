package smu.capstone.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import smu.capstone.common.errorcode.StatusCode;

@Getter
@RequiredArgsConstructor
public class RestApiException extends RuntimeException {
    private final StatusCode statusCode;
}
