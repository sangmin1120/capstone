package smu.capstone.domain.schedule.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;
import smu.capstone.domain.schedule.domain.Schedule;
import smu.capstone.domain.schedule.dto.ScheduleRequestDto;
import smu.capstone.domain.schedule.dto.ScheduleResponseDto;
import smu.capstone.domain.schedule.repository.ScheduleRepository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final InfoService infoService;

    @Transactional
    public ScheduleResponseDto createSchedule(ScheduleRequestDto dto) {
        UserEntity user = infoService.getCurrentUser();

        Schedule schedule = Schedule.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .alertTime(dto.getAlertTime())
                .build();

        return ScheduleResponseDto.from(scheduleRepository.save(schedule));
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getUserSchedules(HttpServletRequest request) {
        UserEntity user = infoService.getCurrentUser();

        return scheduleRepository.findByUser(user).stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedulesByDate(LocalDate date, HttpServletRequest request) {
        UserEntity user = infoService.getCurrentUser();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return scheduleRepository.findByUserAndStartTimeBetween(user, start, end).stream()
                .map(ScheduleResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long scheduleId, ScheduleRequestDto dto, HttpServletRequest request) {
        UserEntity user = infoService.getCurrentUser();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        // 본인 일정만 수정 가능
        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new SecurityException("일정 수정 권한이 없습니다.");
        }

        schedule.setTitle(dto.getTitle());
        schedule.setDescription(dto.getDescription());
        schedule.setStartTime(dto.getStartTime());
        schedule.setAlertTime(dto.getEndTime());
        schedule.setAlertTime(dto.getAlertTime());

        return ScheduleResponseDto.from(scheduleRepository.save(schedule));
    }

    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId, HttpServletRequest request) {
        UserEntity user = infoService.getCurrentUser();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("일정을 찾을 수 없습니다."));

        if (!schedule.getUser().equals(user)) {
            throw new SecurityException("본인의 일정만 삭제할 수 있습니다.");
        }

        scheduleRepository.delete(schedule);
    }

}

