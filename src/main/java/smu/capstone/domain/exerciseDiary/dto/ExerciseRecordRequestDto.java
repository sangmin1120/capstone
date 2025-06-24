package smu.capstone.domain.exerciseDiary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseRecordRequestDto {
    private Long exerciseId; // 선택한 운동 ID
    private int sets;
    private int reps;
}
