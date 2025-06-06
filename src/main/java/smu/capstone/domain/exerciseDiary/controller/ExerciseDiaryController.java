package smu.capstone.domain.exerciseDiary.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.exerciseDiary.dto.ExerciseDiaryRequestDto;
import smu.capstone.domain.exerciseDiary.dto.ExerciseDiaryResponseDto;
import smu.capstone.domain.exerciseDiary.entity.ExerciseDiary;
import smu.capstone.domain.exerciseDiary.service.ExerciseDiaryService;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class ExerciseDiaryController {

    private final ExerciseDiaryService diaryService;

    /**
     * 재활일기 등록
     * POST /api/diaries
     */
    @PostMapping
    public ResponseEntity<ExerciseDiaryResponseDto> createDiary(@RequestBody ExerciseDiaryRequestDto request) {
        ExerciseDiaryResponseDto response = diaryService.createDiary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그인한 유저의 재활일기 목록 조회
     * GET /api/diaries/my
     */
    @GetMapping("/my")
    public ResponseEntity<List<ExerciseDiaryResponseDto>> getMyDiaries() {
        List<ExerciseDiaryResponseDto> diaries = diaryService.getMyDiaries();
        return ResponseEntity.ok(diaries);
    }
}



