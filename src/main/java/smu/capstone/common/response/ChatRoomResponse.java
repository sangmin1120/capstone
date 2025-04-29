package smu.capstone.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import smu.capstone.common.errorcode.StatusCode;

@Builder @Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomResponse<T>{
    private String code;
    private String message;

    private T data;


    public static <T> ResponseEntity<ChatRoomResponse<T>> ok(StatusCode statusCode, T data) {
        ChatRoomResponse<T> response = ChatRoomResponse.<T>builder()
                .code(statusCode.code())
                .message(statusCode.message())
                .data(data)
                .build();
        return ResponseEntity.status(statusCode.status()).body(response);
    }

    public static <T> ResponseEntity<ChatRoomResponse<T>> fail(StatusCode statusCode) {
        ChatRoomResponse<T> response = ChatRoomResponse.<T>builder()
                .code(statusCode.code())
                .message(statusCode.message())
                .build();
        return ResponseEntity.status(statusCode.status()).body(response);
    }
}
