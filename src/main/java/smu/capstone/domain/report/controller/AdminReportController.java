package smu.capstone.domain.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.domain.report.dto.ContentReportResponse;
import smu.capstone.domain.report.dto.DeleteContentRequest;
import smu.capstone.domain.report.entity.ContentReport;
import smu.capstone.domain.report.entity.ContentType;
import smu.capstone.domain.report.service.ReportActionService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportActionService reportActionService;

    // 신고 내역 불러오기
    @GetMapping
    public ResponseEntity<Page<ContentReportResponse>> getReports(
            @PageableDefault(size = 20, sort = "reportedAt", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<ContentReportResponse> responsePage = reportActionService.getAllReportResponses(pageable);
        return ResponseEntity.ok(responsePage);
    }


    // 유저 밴(미구현)
    @PostMapping("/ban/{userId}")
    public ResponseEntity<Void> banUser(@PathVariable Long userId) {
        reportActionService.banUser(userId);
        return ResponseEntity.ok().build();
    }
    // 신고된 컨텐츠 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteContent(@RequestBody DeleteContentRequest request) {
        reportActionService.deleteContent(request.getContentType(), request.getContentId());
        return ResponseEntity.ok().build();
    }

}

