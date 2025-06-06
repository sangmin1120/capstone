package smu.capstone.domain.exerciseDiary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRequestDto {
    private String name;
    private String description;
    private int defaultReps;
    private int defaultSets;
}

