package smu.capstone.domain.exerciseDiary.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.exerciseDiary.dto.ExerciseRecordRequestDto;
import smu.capstone.domain.exerciseDiary.dto.ExerciseRecordResponseDto;
import smu.capstone.domain.exerciseDiary.entity.Exercise;
import smu.capstone.domain.exerciseDiary.entity.ExerciseDiary;
import smu.capstone.domain.exerciseDiary.entity.ExerciseRecord;
import smu.capstone.domain.exerciseDiary.repository.ExerciseDiaryRepository;
import smu.capstone.domain.exerciseDiary.dto.ExerciseDiaryRequestDto;
import smu.capstone.domain.exerciseDiary.dto.ExerciseDiaryResponseDto;
import smu.capstone.domain.exerciseDiary.repository.ExerciseRecordRepository;
import smu.capstone.domain.exerciseDiary.repository.ExerciseRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static smu.capstone.common.errorcode.CommonStatusCode.FORBIDDEN;
import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_EXERCISE_DIARY;

@Service
@RequiredArgsConstructor
public class ExerciseDiaryService {

    private final ExerciseDiaryRepository diaryRepository;
    private final ExerciseRepository exerciseRepository;
    private final InfoService infoService;

    public ExerciseDiaryResponseDto createDiary(ExerciseDiaryRequestDto request) {
        UserEntity user = infoService.getCurrentUser();

        ExerciseDiary diary = ExerciseDiary.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .distance(request.getDistance())
                .date(request.getDate())
                .user(user)
                .build();

        for (ExerciseRecordRequestDto r : request.getRecords()) {
            Exercise exercise = exerciseRepository.findById(r.getExerciseId())
                    .orElseThrow(() -> new RuntimeException("운동을 찾을 수 없습니다."));

            ExerciseRecord record = new ExerciseRecord();
            record.setExercise(exercise);
            record.setReps(r.getReps());
            record.setSets(r.getSets());

            diary.addRecord(record);
        }

        ExerciseDiary saved = diaryRepository.save(diary);

        List<ExerciseRecordResponseDto> recordResponses = saved.getRecords().stream()
                .map(record -> new ExerciseRecordResponseDto(
                        record.getExercise().getId(),
                        record.getExercise().getName(),
                        record.getReps(),
                        record.getSets()
                ))
                .collect(Collectors.toList());

        return new ExerciseDiaryResponseDto(
                saved.getId(),
                saved.getTitle(),
                saved.getContent(),
                saved.getDate(),
                saved.getDistance(),
                recordResponses
        );
    }

    public List<ExerciseDiaryResponseDto> getMyDiaries() {
        UserEntity user = infoService.getCurrentUser();
        List<ExerciseDiary> diaries = diaryRepository.findByUser(user);

        return diaries.stream().map(diary -> {
            List<ExerciseRecordResponseDto> recordResponses = diary.getRecords().stream()
                    .map(record -> new ExerciseRecordResponseDto(
                            record.getExercise().getId(),
                            record.getExercise().getName(),
                            record.getReps(),
                            record.getSets()
                    ))
                    .collect(Collectors.toList());

            return new ExerciseDiaryResponseDto(
                    diary.getId(),
                    diary.getTitle(),
                    diary.getContent(),
                    diary.getDate(),
                    diary.getDistance(),
                    recordResponses
            );
        }).collect(Collectors.toList());
    }
}




