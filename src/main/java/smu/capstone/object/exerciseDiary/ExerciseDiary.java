package smu.capstone.object.exerciseDiary;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import smu.capstone.object.member.domain.UserEntity;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // 운동 기록 작성자

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 운동 기록 내용

    @Column(nullable = true)
    private Double distance; // 이동 거리 (단위: km) - 지도 API 사용 예정

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt; // 생성일

    @UpdateTimestamp
    private LocalDateTime updatedAt; // 수정일

}
