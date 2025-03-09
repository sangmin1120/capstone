package smu.capstone.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.BindingResult;
import smu.capstone.common.errorcode.StatusCode;
import smu.capstone.common.exception.RestApiException;

import java.util.List;

import static smu.capstone.common.errorcode.CommonStatusCode.CREATED;
import static smu.capstone.common.errorcode.CommonStatusCode.OK;


@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseResponse<T> {
    @JsonIgnore
    StatusCode statusCode;

    String code;
    String message;
    T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ValidationErrorDetail> validationExceptionDetails;

    public static BaseResponse<Void> ok() {
        return BaseResponse.<Void>builder()
                .statusCode(OK)
                .build();
    }

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .statusCode(OK)
                .data(data)
                .build();
    }

    public static BaseResponse<Void> created() {
        return BaseResponse.<Void>builder()
                .statusCode(CREATED)
                .build();
    }

    public static <T> BaseResponse<T> created(T data) {
        return BaseResponse.<T>builder()
                .statusCode(CREATED)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> fail(RestApiException restApiException) {
        return BaseResponse.<T>builder()
                .statusCode(restApiException.getStatusCode())
                .build();
    }

    public static BaseResponse<Void> fail(final StatusCode statusCode, final BindingResult bindingResult) {
        return BaseResponse.fail(statusCode, ValidationErrorDetail.of(bindingResult));
    }

    public static BaseResponse<Void> fail(final StatusCode statusCode, final List<ValidationErrorDetail> validationExceptionDetails) {
        return BaseResponse.<Void>builder()
                .statusCode(statusCode)
                .validationExceptionDetails(validationExceptionDetails)
                .build();
    }



    private static <T> CustomBaseResponseBuilder<T> builder() {
        return new CustomBaseResponseBuilder<>();
    }

    private static class CustomBaseResponseBuilder<T> extends BaseResponseBuilder<T> {
        @Override
        public BaseResponse<T> build() {
            BaseResponse<T> baseResponse = super.build();

            StatusCode status = baseResponse.statusCode;
            baseResponse.code = status.code();
            baseResponse.message = status.message();
            return baseResponse;
        }
    }
}
