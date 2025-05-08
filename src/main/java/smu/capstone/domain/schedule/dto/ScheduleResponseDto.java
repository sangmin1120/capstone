package smu.capstone.domain.schedule.dto;

import lombok.Builder;
import lombok.Getter;
import smu.capstone.domain.schedule.domain.Schedule;


import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleResponseDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime alertTime;

    public static ScheduleResponseDto from(Schedule schedule) {
        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .alertTime(schedule.getAlertTime())
                .build();
    }
}
