package smu.capstone.domain.exerciseDiary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRecordResponseDto {
    private Long exerciseId;
    private String exerciseName;
    private int reps;
    private int sets;
}

