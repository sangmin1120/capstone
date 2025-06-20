package smu.capstone.domain.exerciseDiary.dto;

import lombok.Getter;
import smu.capstone.domain.exerciseDiary.entity.ExerciseDiary;

import java.time.LocalDateTime;

@Getter
public class ExerciseDiaryResponseDto {
    private Long id; // 구분하는 id
    private String content; // 운동 기록 내용
    private String description; // 이동 거리 (km)
    private Long sets; // 세트
    private Long reps; // 반복 횟수
    private LocalDateTime createdAt; // 작성 날짜

    public ExerciseDiaryResponseDto(ExerciseDiary exerciseDiary) {
        this.id = exerciseDiary.getId();
        this.content = exerciseDiary.getContent();
        this.description = exerciseDiary.getDescription();
        this.sets = exerciseDiary.getSets();
        this.reps = exerciseDiary.getReps();
        this.createdAt = exerciseDiary.getCreatedAt();

    }
}
