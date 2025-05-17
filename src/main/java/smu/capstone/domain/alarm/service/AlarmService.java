package smu.capstone.domain.alarm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.capstone.intrastructure.fcm.dto.MessageNotification;
import smu.capstone.intrastructure.fcm.dto.NotificationMulticastRequest;
import smu.capstone.intrastructure.fcm.service.FCMService;
import smu.capstone.intrastructure.mail.dto.EmailType;
import smu.capstone.intrastructure.rabbitmq.messaging.MessageSender;

import java.util.List;
import java.util.Map;

/**
 * 디바이스 푸시, 이메일 푸시 서비스
 * 스케줄(디바이스 + 이메일)
 * 댓글(디바이스)
 * 인증번호(이메일)
 */
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final FCMService fcmService;
    private final MessageSender messageSender;

    // 스케줄에 디바이스 푸시, 이메일 푸시
    public void sendSchedule(MessageNotification fcmMessage, String email, Map<String, String> payLoad) {

        // 디바이스 푸시
        fcmService.sendMessage(fcmMessage);
        // 이메일 푸시
        messageSender.sendMessage(email, EmailType.SCHEDULE_ALARM, payLoad);
    }

    // 인증번호에 대한 이메일 푸시
    public void sendAuth(String email, EmailType type, String key) {
        messageSender.sendMessage(email, type, key);
    }

    // 댓글에 대한 디바이스 푸시
    public void sendMessages(NotificationMulticastRequest notifications) {

        fcmService.sendMessages(notifications);
    }
}
