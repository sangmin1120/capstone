package smu.capstone.domain.exerciseDiary.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.domain.exerciseDiary.dto.WalkCompleteRequestDto;
import smu.capstone.domain.exerciseDiary.service.ExerciseDiaryService;

@RestController
@RequestMapping("/api/walk")
@RequiredArgsConstructor
public class WalkController {

    private final ExerciseDiaryService diaryService;

    // 걷기 종료 시 누적 거리 → diary 저장
    @PostMapping("/complete")
    public ResponseEntity<Void> completeWalk(@RequestBody WalkCompleteRequestDto dto) {

        diaryService.createExerciseDiary(dto); // 새로운 운동 기록 만들기

        return ResponseEntity.ok().build();
    }
}