package smu.capstone.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.exception.AppException;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.comment.entity.Comment;
import smu.capstone.domain.comment.repository.CommentRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.domain.member.service.InfoService;
import smu.capstone.domain.report.repository.ContentReportRepository;
import smu.capstone.domain.report.entity.ContentReport;
import smu.capstone.domain.report.entity.ContentType;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final InfoService infoService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final ContentReportRepository contentReportRepository;

    // 컨텐츠와 컨텐츠 생성자 신고
    @Transactional
    public void reportContent(ContentType contentType, Long contentId, String reason) {
        // 현재 로그인 유저
        UserEntity reporter = infoService.getCurrentUser();

        Long reportedUserId;

        if (contentType == ContentType.BOARD) {
            Board board = boardRepository.findById(contentId)
                    .orElseThrow(() -> new AppException(CommonStatusCode.NOT_FOUND_BOARD_ID));
            validateSelfReport(reporter.getId(), board.getUser().getId());
            reportedUserId = board.getUser().getId();
        } else if (contentType == ContentType.COMMENT) {
            Comment comment = commentRepository.findById(contentId)
                    .orElseThrow(() -> new AppException(CommonStatusCode.NOT_FOUND_COMMENT));
            validateSelfReport(reporter.getId(), comment.getUser().getId());
            reportedUserId = comment.getUser().getId();
        } else {
            throw new AppException(CommonStatusCode.INVALID_PARAMETER); // 또는 INVALID_CONTENT_TYPE 정의 시 사용
        }

        UserEntity reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new AppException(CommonStatusCode.NOT_FOUND_USER));

        boolean isDuplicate = contentReportRepository.existsByReporterAndContentTypeAndContentId(
                reporter, contentType, contentId
        );

        if (isDuplicate) {
            throw new AppException(CommonStatusCode.DUPLICATE_REPORT);
        }

        contentReportRepository.save(
                ContentReport.of(reporter, reportedUser, contentType, contentId, reason)
        );
    }
    // 자기 자신 신고 금지
    private void validateSelfReport(Long reporterId, Long targetUserId) {
        if (reporterId.equals(targetUserId)) {
            throw new AppException(CommonStatusCode.CANNOT_REPORT_YOURSELF);
        }
    }
}
