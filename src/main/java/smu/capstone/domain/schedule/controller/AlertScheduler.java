package smu.capstone.domain.schedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import smu.capstone.domain.schedule.domain.Schedule;
import smu.capstone.domain.schedule.repository.ScheduleRepository;
import smu.capstone.domain.schedule.service.ScheduleMailService;
import smu.capstone.domain.schedule.service.WebPushServiceFcm;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleMailService scheduleMailService;
    private final WebPushServiceFcm webPushServiceFcm;

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
            if (fcmToken != null && !fcmToken.isBlank()) {
                webPushServiceFcm.sendPush(fcmToken, "일정 알림", schedule.getTitle() + " 일정이 "+ schedule.getStartTime() +" 에 시작됩니다!");
                System.out.println("FCM token sent: " + fcmToken);
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


