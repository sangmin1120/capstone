package smu.capstone.domain.exerciseDiary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smu.capstone.domain.exerciseDiary.entity.Exercise;
import smu.capstone.domain.member.entity.UserEntity;


import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByUserId(Long userId); // 유저가 등록한 운동 목록 조회용 (옵션)
}

