package smu.capstone.domain.schedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.domain.fcm.dto.MessageNotification;
import smu.capstone.domain.fcm.service.FCMService;
import smu.capstone.domain.schedule.domain.Schedule;
import smu.capstone.domain.schedule.repository.ScheduleRepository;
import smu.capstone.intrastructure.mail.dto.EmailType;
import smu.capstone.intrastructure.rabbitmq.messaging.MessageSender;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final ScheduleRepository scheduleRepository;
    private final FCMService fcmService;
    private final MessageSender messageSender;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndSendAlerts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusSeconds(100);
        LocalDateTime to = now.plusSeconds(100); // 알람 전송이 안됨

        // 아직 전송되지 않은 일정만 조회
        List<Schedule> schedules = scheduleRepository.findByAlertTimeBetweenAndAlertSentIsFalse(from, to);

        for (Schedule schedule : schedules) {
            try {

                String email = schedule.getUser().getEmail();
                String fcmToken = schedule.getUser().getFcmToken();

                // 메일 발송 - rabbitMQ로 변경
                messageSender.sendMessage(email, EmailType.SCHEDULE_ALARM, getMap(schedule));
//            scheduleMailService.sendScheduleAlert(email, schedule);
//                System.out.println("Email sent: " + email);
                if (fcmToken != null && !fcmToken.isBlank()) {
                    fcmService.sendMessage(MessageNotification.of(fcmToken, "일정 알림", schedule.getTitle() + " 일정이 " + schedule.getStartTime() + " 에 시작됩니다!"));
//                    webPushServiceFcm.sendPush(fcmToken, "일정 알림", schedule.getTitle() + " 일정이 " + schedule.getStartTime() + " 에 시작됩니다!");
//                    System.out.println("FCM token sent: " + fcmToken);
                }
                // 중복 방지 플래그 true 로 설정
                schedule.setAlertSent(true);
            } catch (Exception e) {
                log.info("알림 전송 실패: {}", schedule.getId(), e);
            }
            scheduleRepository.save(schedule);
        }
        // 플래그 수정된 일정들 저장
//        scheduleRepository.saveAll(schedules);

        log.info("{}건의 일정 알림 전송 완료", schedules.size());
    }

    private Map<String, String> getMap(Schedule schedule) {
        Map<String, String> map = new HashMap<>();
        map.put("title", schedule.getTitle());
        map.put("startTime", schedule.getStartTime().toString());
        map.put("endTime", schedule.getEndTime().toString());
        map.put("description", schedule.getDescription());
        return map;
    }
}


