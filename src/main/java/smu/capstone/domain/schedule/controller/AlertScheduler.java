package smu.capstone.domain.schedule.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.domain.alarm.service.AlarmService;
import smu.capstone.intrastructure.fcm.dto.MessageNotification;
import smu.capstone.domain.schedule.domain.Schedule;
import smu.capstone.domain.schedule.repository.ScheduleRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertScheduler {

    private final ScheduleRepository scheduleRepository;
    private final AlarmService alarmService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndSendAlerts() {
        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime from = now.minusSeconds(100);
//        LocalDateTime to = now.plusSeconds(100); // 알람 전송이 안됨

        // 아직 전송되지 않은 일정만 조회
        List<Schedule> schedules = scheduleRepository.findByAlertTimeBeforeAndAlertSentIsFalse(now);

        for (Schedule schedule : schedules) {
            try {

                String email = schedule.getUser().getEmail();
                String fcmToken = schedule.getUser().getFcmToken();

                // 알림 푸시
                alarmService.sendSchedule(MessageNotification.of(
                        fcmToken, "일정 알림", schedule.getTitle() + " 일정이 " + schedule.getStartTime() + " 에 시작됩니다!"),
                        email,
                        getMap(schedule));

                // 중복 방지 플래그 true 로 설정
                schedule.setAlertSent(true);
            } catch (Exception e) {
                log.info("알림 전송 실패: {}", schedule.getId(), e);
            }
            // 플래그 수정된 일정들 저장
            scheduleRepository.save(schedule);
        }

//        log.info("{}건의 일정 알림 전송 완료", schedules.size());
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


