package smu.capstone.object.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.object.comment.dto.CommentRequestDto;
import smu.capstone.object.comment.dto.CommentResponseDto;
import smu.capstone.object.comment.service.CommentService;
import smu.capstone.object.member.service.InfoService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments")
public class CommentController {

    private final CommentService commentService;
    private final InfoService infoService;

    //  댓글 작성
    @PostMapping
    public BaseResponse<CommentResponseDto> addComment(@PathVariable("boardId") Long boardId,
                                                       @RequestBody CommentRequestDto requestDto, HttpServletRequest request) {

        Long userId = infoService.getCurrentUser(request).getId();
        CommentResponseDto response = commentService.addComment(boardId, userId, requestDto);
        return BaseResponse.ok(response);
    }

    //  특정 게시글의 모든 댓글 조회
    @GetMapping
    public BaseResponse<List<CommentResponseDto>> getComments(@PathVariable("boardId") Long boardId) {
        List<CommentResponseDto> response = commentService.getComments(boardId);
        return BaseResponse.ok(response);
    }

    //  댓글 삭제
    @DeleteMapping("/{commentId}")
    public BaseResponse<String> deleteComment(@PathVariable("boardId") Long boardId, @PathVariable("commentId") Long commentId
    , HttpServletRequest request) {
        Long userId = infoService.getCurrentUser(request).getId();
        commentService.deleteComment(commentId, userId);
        return BaseResponse.ok("댓글이 삭제되었습니다.");
    }
}
