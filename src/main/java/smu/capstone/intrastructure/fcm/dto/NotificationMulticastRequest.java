package smu.capstone.intrastructure.fcm.dto;

import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
public record NotificationMulticastRequest(
        List<String> targetTokens,
        String title,
        String body
) implements NotificationRequest {

    public static NotificationMulticastRequest of(List<String> tokens, String title, String body) {
        return NotificationMulticastRequest.builder()
                .targetTokens(tokens)
                .title(title)
                .body(body)
                .build();
    }

    public MulticastMessage.Builder buildSendMessage() {
        return MulticastMessage.builder()
                .setNotification(toNotification())
//                .putData("title", title)
//                .putData("body", body)
                .addAllTokens(targetTokens);
    }

    public Notification toNotification() {
        return Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();
    }
}
