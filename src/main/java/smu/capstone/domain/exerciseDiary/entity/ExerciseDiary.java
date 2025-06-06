package smu.capstone.domain.exerciseDiary.entity;

import jakarta.persistence.*;
import lombok.*;
import smu.capstone.common.domain.BaseEntity;
import smu.capstone.domain.member.entity.UserEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercise_diary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // PK

    private String title;
    private String content; // 운동 기록 내용
    private Double distance; // 이동 거리 (단위: km) - 지도 API 사용 예정

    @Column(nullable = false)
    private LocalDate date; // 실제 운동 기록일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 등록한 운동 목록 중 오늘 한 운동 목록 등록
    @Builder.Default
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseRecord> records = new ArrayList<>();

    public void addRecord(ExerciseRecord record) {
        if (!records.contains(record)) {
            records.add(record);
            record.setDiary(this);
        }
    }

}
