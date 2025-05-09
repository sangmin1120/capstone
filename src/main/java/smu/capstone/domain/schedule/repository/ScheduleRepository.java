package smu.capstone.domain.schedule.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.schedule.domain.Schedule;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUser(UserEntity user);
    List<Schedule> findByUserAndStartTimeBetween(UserEntity user, LocalDateTime start, LocalDateTime end);
    List<Schedule> findByAlertTimeBetween(LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = "user")
    List<Schedule> findByAlertTimeBetweenAndAlertSentIsFalse(LocalDateTime start, LocalDateTime end);

}
