package smu.capstone.domain.exerciseDiary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smu.capstone.domain.exerciseDiary.entity.ExerciseDiary;
import smu.capstone.domain.member.entity.UserEntity;

import java.util.List;

@Repository
public interface ExerciseDiaryRepository extends JpaRepository<ExerciseDiary, Long> {
    List<ExerciseDiary> findByUser(UserEntity user);
}
