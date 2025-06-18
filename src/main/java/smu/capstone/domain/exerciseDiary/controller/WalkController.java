package smu.capstone.domain.exerciseDiary.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.domain.exerciseDiary.dto.WalkCompleteRequestDto;
import smu.capstone.domain.exerciseDiary.dto.DistanceUpdateDto;
import smu.capstone.domain.exerciseDiary.service.ExerciseDiaryService;
import smu.capstone.domain.exerciseDiary.service.WalkDistanceService;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

@RestController
@RequestMapping("/api/walk")
@RequiredArgsConstructor
public class WalkController {

    private final ExerciseDiaryService diaryService;
    private final WalkDistanceService walkDistanceService;
    private final InfoService infoService;

    // 실시간 delta 누적
    @PostMapping("/update-distance")
    public ResponseEntity<Void> updateDistance(@RequestBody DistanceUpdateDto dto) {
        UserEntity user = infoService.getCurrentUser();
        walkDistanceService.addDistance(user.getId(), dto.getDelta());
        return ResponseEntity.ok().build();
    }

    // 걷기 종료 시 누적 거리 → diary 저장
    @PostMapping("/complete")
    public ResponseEntity<Void> completeWalk(@RequestBody WalkCompleteRequestDto dto) {
        UserEntity user = infoService.getCurrentUser();
        double totalDistance = walkDistanceService.getTotalDistance(user.getId());

        diaryService.updateDistance(dto.getDiaryId(), totalDistance); // diaryId 직접 사용
        walkDistanceService.clearDistance(user.getId()); // Redis 초기화

        return ResponseEntity.ok().build();
    }
}
