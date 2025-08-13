package smu.capstone.domain.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.board.dto.BoardRequestDto;
import smu.capstone.domain.board.dto.BoardResponseDto;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.entity.BoardType;
import smu.capstone.domain.board.service.AdminBoardService;
import smu.capstone.domain.board.service.AdminNoticeService;
import smu.capstone.domain.board.service.BoardSearchService;
import smu.capstone.domain.comment.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminNoticeService adminNoticeService;
    private final AdminBoardService adminBoardService;
    private final BoardSearchService boardSearchService;
    private final CommentService commentService;

    // ===== 공지사항 =====
    @PostMapping("/notice")
    public BaseResponse<BoardResponseDto> createNotice(@RequestBody BoardRequestDto requestDto) {
        Board board = adminNoticeService.createNotice(requestDto);
        return BaseResponse.ok(new BoardResponseDto(board));
    }

    @PutMapping("/notice/{noticeId}")
    public BaseResponse<Void> updateNotice(@PathVariable Long noticeId,
                                           @RequestBody BoardRequestDto requestDto) {
        adminNoticeService.updateNotice(noticeId, requestDto);
        return BaseResponse.ok();
    }

    @DeleteMapping("/notice/{noticeId}")
    public BaseResponse<Void> deleteNotice(@PathVariable Long noticeId) {
        adminNoticeService.deleteNotice(noticeId);
        return BaseResponse.ok();
    }

    // 페이징 적용해야함
    // ===== 일반 게시글 조회 =====
    @GetMapping("/boards")
    public BaseResponse<List<BoardResponseDto>> getAllBoards() {
        return BaseResponse.ok(boardSearchService.findAllBoards());
    }

    @GetMapping("/boards/type/{boardType}")
    public BaseResponse<List<BoardResponseDto>> getBoardsByType(@PathVariable("boardType") String boardType) {
        BoardType type = BoardType.valueOf(boardType.toUpperCase());
        return BaseResponse.ok(boardSearchService.findBoardsByType(type));
    }

    @GetMapping("/boards/{boardId}")
    public BaseResponse<BoardResponseDto> getBoardById(@PathVariable("boardId") Long boardId) {
        return BaseResponse.ok(boardSearchService.findBoardById(boardId));
    }

    @GetMapping("/boards/search")
    public BaseResponse<List<BoardResponseDto>> searchBoards(@RequestParam String keyword) {
        return BaseResponse.ok(boardSearchService.searchBoards(keyword));
    }

    // ===== 관리자 권한으로 게시글, 댓글 삭제 기능 =====
    @DeleteMapping("/comments/{commentId}")
    public BaseResponse<Void> deleteCommentByAdmin(@PathVariable("commentId") Long commentId) {
        commentService.adminDeleteCommentById(commentId);
        return BaseResponse.ok();
    }

    @DeleteMapping("/boards/{boardId}")
    public BaseResponse<Void> deleteBoard(@PathVariable("boardId") Long boardId) {
        adminBoardService.adminDeleteBoard(boardId);
        return BaseResponse.ok();
    }
}

