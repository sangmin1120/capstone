package smu.capstone.domain.report.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.capstone.domain.report.entity.ContentType;

@Getter
@NoArgsConstructor
public class ReportRequest {
    private ContentType contentType;
    private Long contentId;
    private String reason;
}

