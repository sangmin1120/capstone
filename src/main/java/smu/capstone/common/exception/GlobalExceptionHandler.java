package smu.capstone.common.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.response.BaseResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RestApiException.class)
    public BaseResponse<Void> handleCustomException(RestApiException restApiException, HttpServletResponse response){

        response.setStatus(restApiException.getStatusCode().status().value());
        return BaseResponse.fail(restApiException);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletResponse response) {

        response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        return BaseResponse.fail(CommonStatusCode.VALIDATION_FAILED, e.getBindingResult());
    }
}
