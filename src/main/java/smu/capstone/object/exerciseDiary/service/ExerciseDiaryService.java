package smu.capstone.object.exerciseDiary.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.exerciseDiary.domain.ExerciseDiary;
import smu.capstone.object.exerciseDiary.repository.ExerciseDiaryRepository;
import smu.capstone.object.exerciseDiary.dto.ExerciseDiaryRequestDto;
import smu.capstone.object.exerciseDiary.dto.ExerciseDiaryResponseDto;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.service.InfoService;

import java.util.List;
import java.util.stream.Collectors;

import static smu.capstone.common.errorcode.CommonStatusCode.FORBIDDEN;
import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_EXERCISE_DIARY;

@Service
@RequiredArgsConstructor
public class ExerciseDiaryService {

    private final ExerciseDiaryRepository exerciseDiaryRepository;
    private final InfoService infoService;

    // 운동 기록 작성
    public ExerciseDiaryResponseDto createExerciseDiary(ExerciseDiaryRequestDto requestDto) {
        UserEntity user = infoService.getCurrentUser();

        ExerciseDiary diary = ExerciseDiary.builder()
                .user(user)
                .content(requestDto.getContent())
                .distance(requestDto.getDistance())
                .build();

        return new ExerciseDiaryResponseDto(exerciseDiaryRepository.save(diary));
    }

    // 본인의 운동 기록 목록 조회
    public List<ExerciseDiaryResponseDto> getUserDiaries() {
        UserEntity currentUser = infoService.getCurrentUser();

        List<ExerciseDiary> diaries = exerciseDiaryRepository.findByUser(currentUser);
        return diaries.stream().map(ExerciseDiaryResponseDto::new).collect(Collectors.toList());
    }

    // 운동 기록 수정
    @Transactional
    public ExerciseDiaryResponseDto updateDiary(Long diaryId, ExerciseDiaryRequestDto requestDto) {
        UserEntity currentUser = infoService.getCurrentUser();

        ExerciseDiary diary = exerciseDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_EXERCISE_DIARY));

        // 본인만 수정 가능하도록 체크
        if (!diary.getUser().equals(currentUser)) {
            throw new RestApiException(FORBIDDEN);
        }

        diary.setContent(requestDto.getContent());
        diary.setDistance(requestDto.getDistance());

        return new ExerciseDiaryResponseDto(exerciseDiaryRepository.save(diary));
    }

    // 운동 기록 삭제
    @Transactional
    public void deleteDiary(Long diaryId) {
        UserEntity currentUser = infoService.getCurrentUser();

        ExerciseDiary diary = exerciseDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_EXERCISE_DIARY));

        //  본인만 삭제 가능하도록 체크
        if (!diary.getUser().equals(currentUser)) {
            throw new RestApiException(FORBIDDEN);
        }

        exerciseDiaryRepository.delete(diary);
    }
}

