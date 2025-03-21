package smu.capstone.object.exerciseDiary.dto;

import lombok.Getter;
import smu.capstone.object.exerciseDiary.domain.ExerciseDiary;

import java.time.LocalDateTime;

@Getter
public class ExerciseDiaryResponseDto {
    private String content; // 운동 기록 내용
    private Double distance; // 이동 거리 (km)
    private LocalDateTime createdAt; // 작성 날짜

    public ExerciseDiaryResponseDto(ExerciseDiary exerciseDiary) {
        this.content = exerciseDiary.getContent();
        this.distance = exerciseDiary.getDistance();
        this.createdAt = exerciseDiary.getCreatedAt();

    }
}
