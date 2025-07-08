package smu.capstone.domain.schedule.domain;

import jakarta.persistence.*;
import lombok.*;
import smu.capstone.domain.member.entity.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 일정 소유자 (재활 환자 등)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // 일정 제목
    @Column(nullable = false)
    private String title;

    // 일정 내용
    @Column(columnDefinition = "TEXT")
    private String description;

    // 일정 시작 시간
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    // 일정 종료 시간
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // 알림 시간
    @Column(name = "alert_time")
    private LocalDateTime alertTime;

    // 알림 메일 전송 여부
    @Builder.Default
    @Column(name = "alert_sent")
    private boolean alertSent = false;
}

