package smu.capstone.domain.exerciseDiary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import smu.capstone.domain.exerciseDiary.entity.ExerciseRecord;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
}
