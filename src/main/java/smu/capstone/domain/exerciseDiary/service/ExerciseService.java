package smu.capstone.domain.exerciseDiary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.domain.exerciseDiary.dto.ExerciseRequestDto;
import smu.capstone.domain.exerciseDiary.dto.ExerciseResponseDto;
import smu.capstone.domain.exerciseDiary.entity.Exercise;
import smu.capstone.domain.exerciseDiary.repository.ExerciseRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final InfoService infoService;

    public ExerciseResponseDto createExercise(ExerciseRequestDto request) {
        UserEntity user = infoService.getCurrentUser();

        Exercise exercise = Exercise.builder()
                .name(request.getName())
                .description(request.getDescription())
                .defaultReps(request.getDefaultReps())
                .defaultSets(request.getDefaultSets())
                .user(user)
                .build();

        Exercise saved = exerciseRepository.save(exercise);

        return new ExerciseResponseDto(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getDefaultReps(),
                saved.getDefaultSets()
        );
    }

    public List<ExerciseResponseDto> getMyExercises() {
        UserEntity user = infoService.getCurrentUser();
        List<Exercise> exercises = exerciseRepository.findByUserId(user.getId());

        return exercises.stream()
                .map(e -> new ExerciseResponseDto(
                        e.getId(), e.getName(), e.getDescription(), e.getDefaultReps(), e.getDefaultSets()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateExercise(Long exerciseId, ExerciseRequestDto dto) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("운동 ID를 찾을 수 없습니다."));

        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
    }

    @Transactional
    public void deleteExercise(Long exerciseId) {
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new IllegalArgumentException("운동 ID를 찾을 수 없습니다."));

        exerciseRepository.delete(exercise);
    }
}




