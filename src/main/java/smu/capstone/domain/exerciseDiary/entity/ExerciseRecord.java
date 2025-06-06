package smu.capstone.domain.exerciseDiary.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ExerciseRecord {
    // 재활 일기와 등록한 운동 사이에서 실제 행한 세트수, 세트당 개수를 등록

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private ExerciseDiary diary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;


    private int reps;
    private int sets;
}

