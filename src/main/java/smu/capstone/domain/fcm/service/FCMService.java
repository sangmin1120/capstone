package smu.capstone.domain.fcm.service;


import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.fcm.dto.MessageNotification;
import smu.capstone.domain.fcm.dto.NotificationRequest;

import static smu.capstone.common.errorcode.FcmExceptionCode.FCM_SERVICE_UNAVAILABLE;

@Service
@RequiredArgsConstructor
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    // 단일 메시지 처리 -> 메시지 title,body 만드는 메서드 만들기
    public void sendMessage(MessageNotification request) {
        try {
            Message message = request.buildMessage()
                    .setApnsConfig(getApnsConfig(request)) // ios
//                    .setWebpushConfig(getWebpushConfig(request)) // 웹 푸시용 추가
                    .build();
            firebaseMessaging.sendAsync(message);
        } catch (RuntimeException exception) {
            throw new RestApiException(FCM_SERVICE_UNAVAILABLE);
        }
    }

    // 여러 기기 전송
    /*
    public void sendMessages(final NotificationMulticastRequest request) {
        try {
            val messages = request.buildSendMessage().setApnsConfig(getApnsConfig(request)).build();
            firebaseMessaging.sendMulticastAsync(messages);
        } catch (RuntimeException exception) {
            throw new FcmException(FCM_SERVICE_UNAVAILABLE, exception.getMessage());
        }
    }
     */

    /*
    // 웹 설정
    private WebpushConfig getWebpushConfig(NotificationRequest request) {
        return WebpushConfig.builder()
                .putHeader("Urgency", "high")
                .setNotification(new WebpushNotification(
                        request.title(), // 알림 제목
                        request.body(),  // 알림 내용
                        "https://yourdomain.com/icon.png" // 아이콘 URL
                ))
                .setFcmOptions(WebpushFcmOptions.withLink("https://yourdomain.com/your-page"))
                .build();
    }
    */

    // ios 설정
    private ApnsConfig getApnsConfig(NotificationRequest request) {
        val alert = ApsAlert.builder().setTitle(request.title()).setBody(request.body()).build();
        val aps = Aps.builder().setAlert(alert).setSound("default").build();
        return ApnsConfig.builder().setAps(aps).build();
    }
}
