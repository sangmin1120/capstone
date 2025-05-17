package smu.capstone.intrastructure.fcm.service;


import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.intrastructure.fcm.dto.MessageNotification;
import smu.capstone.intrastructure.fcm.dto.NotificationMulticastRequest;
import smu.capstone.intrastructure.fcm.dto.NotificationRequest;

import static smu.capstone.common.errorcode.FcmExceptionCode.FCM_SERVICE_UNAVAILABLE;

/**
 * token 값에 따라 notification 전송
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FCMService {

    private final FirebaseMessaging firebaseMessaging;

    // 단일 메시지 처리 -> 메시지 title,body 만드는 메서드 만들기
    public void sendMessage(MessageNotification request) {

        //Fcm 토큰 유효성 검사
        if (request.targetToken()==null || request.targetToken().isEmpty()) {
            log.info("[warn FCM] FCM token is Empty");
            return;
        }

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

    // 여러 기기 전송 -> 테스트를 못해봄
    public void sendMessages(final NotificationMulticastRequest request) {

        // 1. null 체크
        if (request == null || request.targetTokens() == null || request.targetTokens().isEmpty()) {
            log.warn("[FCM] 요청이 null이거나 FCM 토큰이 없습니다. 전송하지 않습니다.");
            return;
        }

        try {
            log.info("Fcm alarm start");

//            MulticastMessage messages = request.buildSendMessage().setApnsConfig(getApnsConfig(request)).build();

            for (String token : request.targetTokens()) {
                Message message = Message.builder()
                        .setNotification(request.toNotification())
                        .setToken(token)
                        .build();
                firebaseMessaging.sendAsync(message);
            } // -> 반복문으로 하면 되는데 sendMulticast은 동작을 안함
//            firebaseMessaging.sendMulticast(messages);

            log.info("Fcm alarm end");
        } catch (RuntimeException exception) {
            log.error("[FCM] 예외 발생: {}", exception.getMessage());
            throw new RestApiException(FCM_SERVICE_UNAVAILABLE);
        }
    }

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
