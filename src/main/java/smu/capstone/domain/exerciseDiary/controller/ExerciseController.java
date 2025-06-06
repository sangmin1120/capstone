package smu.capstone.domain.exerciseDiary.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.exerciseDiary.dto.ExerciseRequestDto;
import smu.capstone.domain.exerciseDiary.dto.ExerciseResponseDto;
import smu.capstone.domain.exerciseDiary.service.ExerciseService;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    /**
     * 운동 등록
     * POST /api/exercises
     */
    @PostMapping
    public ResponseEntity<ExerciseResponseDto> createExercise(@RequestBody ExerciseRequestDto request) {
        ExerciseResponseDto response = exerciseService.createExercise(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인한 유저의 운동 목록 조회
     * GET /api/exercises/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<ExerciseResponseDto>> getMyExercises() {
        List<ExerciseResponseDto> exercises = exerciseService.getMyExercises();
        return ResponseEntity.ok(exercises);
    }
}

