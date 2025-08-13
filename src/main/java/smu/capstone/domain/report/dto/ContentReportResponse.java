package smu.capstone.domain.report.dto;

import lombok.Getter;
import lombok.Setter;
import smu.capstone.domain.report.entity.ContentReport;
import smu.capstone.domain.report.entity.ContentType;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContentReportResponse {
    private Long id;
    private String reporterName;
    private String reportedUserName;
    private ContentType contentType;
    private Long contentId;
    private String reason;
    private LocalDateTime reportedAt;
    private boolean contentDeleted;


    public static ContentReportResponse from(ContentReport report,
                                             boolean isDeleted) {
        ContentReportResponse dto = new ContentReportResponse();
        dto.setId(report.getId());
        dto.setReporterName(report.getReporter().getUsername());
        dto.setReportedUserName(report.getReportedUser().getUsername());
        dto.setContentType(report.getContentType());
        dto.setContentId(report.getContentId());
        dto.setReason(report.getContent());
        dto.setReportedAt(report.getReportedAt());
        dto.setContentDeleted(isDeleted);
        return dto;
    }

}
