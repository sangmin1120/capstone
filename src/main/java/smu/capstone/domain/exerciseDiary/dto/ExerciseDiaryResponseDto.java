package smu.capstone.domain.exerciseDiary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.capstone.domain.exerciseDiary.entity.ExerciseDiary;
import smu.capstone.domain.exerciseDiary.entity.ExerciseRecord;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDiaryResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDate date;
    private Double distance;
    private List<ExerciseRecordResponseDto> records;
}




