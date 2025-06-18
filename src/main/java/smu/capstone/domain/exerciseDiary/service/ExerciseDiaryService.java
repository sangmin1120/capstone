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
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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

    @Transactional
    public void deleteDiary(Long diaryId) {
        ExerciseDiary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 다이어리 ID"));

        diaryRepository.delete(diary);
    }

    @Transactional
    public void updateDiary(Long diaryId, ExerciseDiaryRequestDto dto) {
        ExerciseDiary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 다이어리 ID"));

        // 기존 기록 삭제 (Cascade + orphanRemoval 적용됨)
        diary.getRecords().clear();

        // 새로운 기록 생성
        List<ExerciseRecord> newRecords = dto.getRecords().stream()
                .map(recordDto -> {
                    Exercise exercise = exerciseRepository.findById(recordDto.getExerciseId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 운동 ID"));

                    ExerciseRecord record = new ExerciseRecord();
                    record.setDiary(diary);
                    record.setExercise(exercise);
                    record.setReps(recordDto.getReps());
                    record.setSets(recordDto.getSets());
                    return record;
                }).toList();


        diary.setTitle(dto.getTitle());
        diary.setContent(dto.getContent());
        diary.setDate(dto.getDate());
        diary.setDistance(dto.getDistance());
        diary.getRecords().addAll(newRecords);
    }

    @Transactional(readOnly = true)
    public ExerciseDiaryResponseDto getDiaryByDate(LocalDate date) {
        UserEntity user = infoService.getCurrentUser(); // 현재 로그인 유저 기준

        ExerciseDiary diary = diaryRepository.findByUserAndDate(user, date);

        List<ExerciseRecordResponseDto> recordDtos = diary.getRecords().stream()
                .map(record -> new ExerciseRecordResponseDto(
                        record.getExercise().getId(),
                        record.getExercise().getName(),
                        record.getReps(),
                        record.getSets()
                ))
                .toList();

        return new ExerciseDiaryResponseDto(
                diary.getId(),
                diary.getTitle(),
                diary.getContent(),
                diary.getDate(),
                diary.getDistance(),
                recordDtos
        );
    }

    public void saveDistanceToDiary(Long userId, double distance, String date) {
        ExerciseDiary diary = diaryRepository.findByUserAndDate(infoService.getCurrentUser(), LocalDate.parse(date));
        diary.setDistance(distance);
        diaryRepository.save(diary);
    }

    public void updateDistance(Long diaryId, double distance) {
        ExerciseDiary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("일기를 찾을 수 없습니다."));
        diary.setDistance(distance);
        diaryRepository.save(diary);
    }
}




