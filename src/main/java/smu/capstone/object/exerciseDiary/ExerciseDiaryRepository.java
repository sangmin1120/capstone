package smu.capstone.object.exerciseDiary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smu.capstone.object.member.domain.UserEntity;

import java.util.List;

@Repository
public interface ExerciseDiaryRepository extends JpaRepository<ExerciseDiary, Long> {
    List<ExerciseDiary> findByUser(UserEntity user);
}
