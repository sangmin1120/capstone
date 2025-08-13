package smu.capstone.domain.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.domain.report.dto.ReportRequest;
import smu.capstone.domain.report.service.ReportService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    /**
     * 게시글 또는 댓글을 신고
     * @param request 신고 요청 본문
     */
    @PostMapping
    public ResponseEntity<Void> reportContent(@RequestBody ReportRequest request) {
        reportService.reportContent(
                request.getContentType(),
                request.getContentId(),
                request.getReason()
        );
        return ResponseEntity.ok().build(); // or return new ResponseEntity<>(HttpStatus.CREATED);
    }
}

