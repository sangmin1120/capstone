package smu.capstone.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.AppException;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.comment.repository.CommentRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.domain.member.service.InfoService;
import smu.capstone.domain.report.repository.ContentReportRepository;
import smu.capstone.domain.report.dto.ContentReportResponse;
import smu.capstone.domain.report.entity.ContentReport;
import smu.capstone.domain.report.entity.ContentType;

@Service
@RequiredArgsConstructor
public class ReportActionService {
    // 신고 받은 내용을 ADMIN 이 처리하는 클래스

    private final ContentReportRepository contentReportRepository;
    private final InfoService infoService;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    // 신고 내용 불러오기
    public Page<ContentReportResponse> getAllReportResponses(Pageable pageable) {
        Page<ContentReport> reports = contentReportRepository.findAll(pageable);

        return reports.map(report -> {
            boolean isDeleted = switch (report.getContentType()) {
                case BOARD -> !boardRepository.existsById(report.getContentId());
                case COMMENT -> !commentRepository.existsById(report.getContentId());
            };
            return ContentReportResponse.from(report, isDeleted);
        });
    }

    // 유저 밴하는 함수(아직 구현 안되어 있어 주석처리)
    @Transactional
    public void banUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(CommonStatusCode.NOT_FOUND_USER));
        //user.changeActivity(UserActivity.BAN);
    }

    // 신고 받은 게시물 or 댓글 삭제
    @Transactional
    public void deleteContent(ContentType type, Long contentId) {
        if (type == ContentType.BOARD) {
            if (!boardRepository.existsById(contentId)) {
                throw new AppException(CommonStatusCode.NOT_FOUND_BOARD_ID);
            }
            boardRepository.deleteById(contentId);
        } else if (type == ContentType.COMMENT) {
            if (!commentRepository.existsById(contentId)) {
                throw new AppException(CommonStatusCode.NOT_FOUND_COMMENT);
            }
            commentRepository.deleteById(contentId);
        } else {
            throw new AppException(CommonStatusCode.INVALID_PARAMETER); // 예외 처리
        }
    }
}
