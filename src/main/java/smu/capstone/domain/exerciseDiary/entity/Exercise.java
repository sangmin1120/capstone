package smu.capstone.domain.exerciseDiary.entity;

import jakarta.persistence.*;
import lombok.*;
import smu.capstone.domain.member.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
    public class Exercise {

    @Id @GeneratedValue
    private Long id;

    private String name;
    private String description; // 운동 설명
    private int defaultReps; // 기본 권장 횟수
    private int defaultSets; // 기본 권장 세트수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseRecord> records = new ArrayList<>();

    @Builder
    public Exercise(String name, String description, int defaultReps, int defaultSets, UserEntity user) {
        this.name = name;
        this.description = description;
        this.defaultReps = defaultReps;
        this.defaultSets = defaultSets;
        this.user = user;
    }
}

