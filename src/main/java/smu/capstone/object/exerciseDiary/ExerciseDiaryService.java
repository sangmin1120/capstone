package smu.capstone.object.exerciseDiary;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.service.InfoService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExerciseDiaryService {

    private final ExerciseDiaryRepository exerciseDiaryRepository;
    private final InfoService infoService;

    // 운동 기록 작성
    public ExerciseDiaryResponseDto createExerciseDiary(ExerciseDiaryRequestDto requestDto, HttpServletRequest request) {
        UserEntity user = infoService.getCurrentUser(request);

        ExerciseDiary diary = ExerciseDiary.builder()
                .user(user)
                .content(requestDto.getContent())
                .distance(requestDto.getDistance())
                .build();

        return new ExerciseDiaryResponseDto(exerciseDiaryRepository.save(diary));
    }

    // 본인의 운동 기록 목록 조회
    public List<ExerciseDiaryResponseDto> getUserDiaries(HttpServletRequest request) {
        UserEntity currentUser = infoService.getCurrentUser(request);

        List<ExerciseDiary> diaries = exerciseDiaryRepository.findByUser(currentUser);
        return diaries.stream().map(ExerciseDiaryResponseDto::new).collect(Collectors.toList());
    }

    // 운동 기록 수정
    @Transactional
    public ExerciseDiaryResponseDto updateDiary(Long diaryId, ExerciseDiaryRequestDto requestDto, HttpServletRequest request) {
        UserEntity currentUser = infoService.getCurrentUser(request);

        ExerciseDiary diary = exerciseDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("운동 기록을 찾을 수 없습니다."));

        // 본인만 수정 가능하도록 체크
        if (!diary.getUser().equals(currentUser)) {
            throw new SecurityException("해당 운동 기록을 수정할 권한이 없습니다.");
        }

        diary.setContent(requestDto.getContent());
        diary.setDistance(requestDto.getDistance());

        return new ExerciseDiaryResponseDto(exerciseDiaryRepository.save(diary));
    }

    // 운동 기록 삭제
    @Transactional
    public void deleteDiary(Long diaryId, HttpServletRequest request) {
        UserEntity currentUser = infoService.getCurrentUser(request);

        ExerciseDiary diary = exerciseDiaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("운동 기록을 찾을 수 없습니다."));

        //  본인만 삭제 가능하도록 체크
        if (!diary.getUser().equals(currentUser)) {
            throw new SecurityException("해당 운동 기록을 삭제할 권한이 없습니다.");
        }

        exerciseDiaryRepository.delete(diary);
    }
}

