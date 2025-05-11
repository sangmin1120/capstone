package smu.capstone.domain.fcm.dto;

import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NonNull;

/**
 * token, notification 형태로 전달
 * @param targetToken
 * @param title
 * @param body
 */
@Builder(access = AccessLevel.PRIVATE)
public record MessageNotification(
        @NonNull String targetToken,
        String title,
        String body
) implements NotificationRequest {

    public static MessageNotification of(String token, String title, String body) {
        return MessageNotification.builder()
                .targetToken(token)
                .title(title)
                .body(body)
                .build();
    }

    public Message.Builder buildMessage() {
        return Message.builder()
                .setToken(targetToken)
                .setNotification(toNotification());
    }

    public Notification toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
}
