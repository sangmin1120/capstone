package smu.capstone.domain.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.comment.dto.CommentRequestDto;
import smu.capstone.domain.comment.dto.CommentResponseDto;
import smu.capstone.domain.comment.service.CommentService;
import smu.capstone.domain.member.service.InfoService;
import smu.capstone.domain.member.util.LoginUserUtil;

import java.util.List;

import static smu.capstone.domain.member.util.LoginUserUtil.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards/{boardId}/comments")
public class CommentController {

    private final CommentService commentService;


    //  댓글 작성
    @PostMapping
    public BaseResponse<CommentResponseDto> addComment(@PathVariable("boardId") Long boardId,
                                                       @RequestBody CommentRequestDto requestDto) {
        Long userId = getLoginMemberId();
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
    public BaseResponse<String> deleteComment(@PathVariable("boardId") Long boardId, @PathVariable("commentId") Long commentId) {
        Long userId = getLoginMemberId();
        commentService.deleteComment(commentId, userId);
        return BaseResponse.ok("댓글이 삭제되었습니다.");
    }

    @PutMapping("/{commentId}")
    public BaseResponse<String> updateComment(@PathVariable("boardId") Long boardId, @PathVariable("commentId") Long commentId,  @RequestBody CommentRequestDto requestDto) {
        Long userId = getLoginMemberId();
        commentService.updateComment(commentId, userId, requestDto);
        return BaseResponse.ok("댓글이 수정되었습니다.");
    }
}
