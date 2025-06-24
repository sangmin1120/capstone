package smu.capstone.domain.exerciseDiary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WalkCompleteRequestDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String distance;
}