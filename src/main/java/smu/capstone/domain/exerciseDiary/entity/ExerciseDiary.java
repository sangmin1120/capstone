package smu.capstone.domain.exerciseDiary.entity;

import jakarta.persistence.*;
import lombok.*;
import smu.capstone.common.domain.BaseEntity;
import smu.capstone.domain.member.entity.UserEntity;

@Entity
@Table(name = "exercise_diary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDiary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // 운동 기록 작성자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 운동 기록 내용

    @Column(nullable = true)
    private Double distance; // 이동 거리 (단위: km) - 지도 API 사용 예정

}
