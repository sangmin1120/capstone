package smu.capstone.object.exerciseDiary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor  // 기본 생성자 추가
@AllArgsConstructor // 모든 필드를 포함하는 생성자 추가 (선택)
public class ExerciseDiaryRequestDto {

    private String content; // 운동 기록 내용
    private Double distance; // 이동 거리 (km) -> 기록 저장, 수정에서 사용?
    // private List<GPSLocation> locations;  // GPS 좌표 리스트 추가
    // GPSLocation 클래스 만들고 구현 시작해야함..
}
