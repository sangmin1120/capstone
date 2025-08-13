package smu.capstone.domain.report.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.capstone.domain.report.entity.ContentType;

@Getter
@NoArgsConstructor
public class DeleteContentRequest {

    private ContentType contentType;
    private Long contentId;

    // 생성자 없이 Lombok 사용 + 기본 생성자로 직렬화 지원 (RequestBody 매핑 가능)
}

