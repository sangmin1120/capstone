package smu.capstone.object.exerciseDiary.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.object.exerciseDiary.dto.ExerciseDiaryRequestDto;
import smu.capstone.object.exerciseDiary.dto.ExerciseDiaryResponseDto;
import smu.capstone.object.exerciseDiary.service.ExerciseDiaryService;

import java.util.List;

@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseDiaryController {

    private final ExerciseDiaryService exerciseDiaryService;

    @PostMapping
    public BaseResponse<ExerciseDiaryResponseDto> createDiary(@RequestBody ExerciseDiaryRequestDto requestDto, HttpServletRequest request) {
        ExerciseDiaryResponseDto response = exerciseDiaryService.createExerciseDiary(requestDto, request);
        return BaseResponse.ok(response);
    }

    // 사용자의 모든 운동 기록 조회
    @GetMapping
    public BaseResponse<List<ExerciseDiaryResponseDto>> getUserDiaries(HttpServletRequest request) {
        List<ExerciseDiaryResponseDto> diaries = exerciseDiaryService.getUserDiaries(request);
        return BaseResponse.ok(diaries);
    }

    // 운동 기록 수정
    @PutMapping("/{diaryId}")
    public BaseResponse<ExerciseDiaryResponseDto> updateDiary(
            @PathVariable("diaryId") Long diaryId,
            @RequestBody ExerciseDiaryRequestDto requestDto, HttpServletRequest request) {
        ExerciseDiaryResponseDto updatedDiary = exerciseDiaryService.updateDiary(diaryId, requestDto, request);
        return BaseResponse.ok(updatedDiary);
    }

    // 운동 기록 삭제
    @DeleteMapping("/{diaryId}")
    public BaseResponse<String> deleteDiary(@PathVariable("diaryId") Long diaryId, HttpServletRequest request) {
        exerciseDiaryService.deleteDiary(diaryId, request);
        return BaseResponse.ok("운동 기록이 삭제되었습니다.");
    }
}

