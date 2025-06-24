package smu.capstone.domain.schedule.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ScheduleRequestDto {
    private String title;
    private String description;
    private LocalDateTime startTime; // date 포맷 수정하기
    private LocalDateTime endTime;
    private LocalDateTime alertTime;
}
