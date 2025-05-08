package smu.capstone.domain.schedule.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.domain.schedule.dto.ScheduleRequestDto;
import smu.capstone.domain.schedule.dto.ScheduleResponseDto;
import smu.capstone.domain.schedule.service.ScheduleService;


import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 등록
    @PostMapping
    public ResponseEntity<ScheduleResponseDto> createSchedule(
            @RequestBody ScheduleRequestDto dto,HttpServletRequest request) {
        return ResponseEntity.ok(scheduleService.createSchedule(dto));
    }

    // 사용자 전체 일정 조회
    @GetMapping
    public ResponseEntity<List<ScheduleResponseDto>> getUserSchedules(HttpServletRequest request) {
        return ResponseEntity.ok(scheduleService.getUserSchedules(request));
    }

    // 날짜별 일정 조회
    @GetMapping("/date")
    public ResponseEntity<List<ScheduleResponseDto>> getSchedulesByDate(
            @RequestParam("date") String dateStr,
            HttpServletRequest request) {
        LocalDate date = LocalDate.parse(dateStr);
        return ResponseEntity.ok(scheduleService.getSchedulesByDate(date, request));
    }

    // 일정 수정
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
            @PathVariable("scheduleId") Long scheduleId,
            @RequestBody ScheduleRequestDto dto,
            HttpServletRequest request) {
        return ResponseEntity.ok(scheduleService.updateSchedule(scheduleId, dto, request));
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(
            @PathVariable("scheduleId") Long scheduleId,
            HttpServletRequest request) {
        scheduleService.deleteSchedule(scheduleId, request);
        return ResponseEntity.ok("일정이 삭제되었습니다.");
    }
}
