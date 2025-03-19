package smu.capstone.object.exerciseDiary;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseDiaryController {

    private final ExerciseDiaryService exerciseDiaryService;

    @PostMapping
    public ResponseEntity<ExerciseDiaryResponseDto> createDiary(@RequestBody ExerciseDiaryRequestDto requestDto, HttpServletRequest request) {
        ExerciseDiaryResponseDto response = exerciseDiaryService.createExerciseDiary(requestDto, request);
        return ResponseEntity.ok(response);
    }

    // 사용자의 모든 운동 기록 조회
    @GetMapping
    public ResponseEntity<List<ExerciseDiaryResponseDto>> getUserDiaries(HttpServletRequest request) {
        List<ExerciseDiaryResponseDto> diaries = exerciseDiaryService.getUserDiaries(request);
        return ResponseEntity.ok(diaries);
    }

    // 운동 기록 수정
    @PutMapping("/{diaryId}")
    public ResponseEntity<ExerciseDiaryResponseDto> updateDiary(
            @PathVariable("diaryId") Long diaryId,
            @RequestBody ExerciseDiaryRequestDto requestDto, HttpServletRequest request) {
        ExerciseDiaryResponseDto updatedDiary = exerciseDiaryService.updateDiary(diaryId, requestDto, request);
        return ResponseEntity.ok(updatedDiary);
    }

    // 운동 기록 삭제
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<String> deleteDiary(@PathVariable Long diaryId, HttpServletRequest request) {
        exerciseDiaryService.deleteDiary(diaryId, request);
        return ResponseEntity.ok("운동 기록이 삭제되었습니다.");
    }
}

