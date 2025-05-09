package smu.capstone.domain.schedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import smu.capstone.domain.fcm.dto.MessageNotification;
import smu.capstone.domain.fcm.service.FCMService;
import smu.capstone.domain.schedule.domain.Schedule;
import smu.capstone.domain.schedule.repository.ScheduleRepository;
import smu.capstone.domain.schedule.service.ScheduleMailService;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMailService scheduleMailService;
    //private final WebPushServiceFcm webPushServiceFcm;
    private final FCMService fcmService;

    @Scheduled(fixedRate = 60000)
    public void checkAndSendAlerts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusSeconds(30);
        LocalDateTime to = now.plusSeconds(30);

        // 아직 전송되지 않은 일정만 조회
        List<Schedule> schedules = scheduleRepository.findByAlertTimeBetweenAndAlertSentIsFalse(from, to);

        for (Schedule schedule : schedules) {
            try {

            String email = schedule.getUser().getEmail();
            String fcmToken = schedule.getUser().getFcmToken();

            // 메일 발송
            scheduleMailService.sendScheduleAlert(email, schedule);
            System.out.println("Email sent: " + email);
            // FCM Web Push 알림 전송
            if (fcmToken != null && !fcmToken.isBlank()) {
                String title = "일정 알림";
                String body = schedule.getTitle() + " 일정이 " + schedule.getStartTime() + "에 시작됩니다!";
                MessageNotification notification = MessageNotification.of(fcmToken, title, body);
                fcmService.sendMessage(notification); // 상민님이 만든 message 보내는 기능
                log.info("WebPush sent to token: {}", fcmToken);
            }
            // 중복 방지 플래그 true 로 설정
            schedule.setAlertSent(true);
            } catch (Exception e) {
                log.error("알림 전송 실패: {}", schedule.getId(), e);
            }
        }
        // 플래그 수정된 일정들 저장
        scheduleRepository.saveAll(schedules);

        log.info("{}건의 일정 알림 전송 완료", schedules.size());
    }
}


