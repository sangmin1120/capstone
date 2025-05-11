package smu.capstone.domain.schedule.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.domain.fcm.dto.MessageNotification;
import smu.capstone.domain.fcm.service.FCMService;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class PushTestController {

    private final FCMService fcmService;
    private final InfoService infoService;

    @PostMapping("/send-push")
    public ResponseEntity<Void> testPush(HttpServletRequest request) {
        UserEntity user = infoService.getCurrentUser();
        String token = user.getFcmToken();
        if (token != null && !token.isBlank()) {
            fcmService.sendMessage(MessageNotification.of(token, "테스트 알림", "이건 수동으로 전송된 웹 푸시 알림입니다."));
//            webPushServiceFcm.sendPush(token, "테스트 알림", "이건 수동으로 전송된 웹 푸시 알림입니다.");
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}

