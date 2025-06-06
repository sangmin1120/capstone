package smu.capstone.domain.exerciseDiary.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDiaryRequestDto {
    private String title;
    private String content;
    private Double distance;
    private LocalDate date;
    private List<ExerciseRecordRequestDto> records; // 운동 기록 목록
}


