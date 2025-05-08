package smu.capstone.domain.fcm.dto;

import com.google.firebase.messaging.Notification;

public interface NotificationRequest {
    String title();
    String body();
    Notification toNotification();
}
