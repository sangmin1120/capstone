package smu.capstone.domain.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.comment.entity.Comment;
import smu.capstone.domain.comment.dto.CommentRequestDto;
import smu.capstone.domain.comment.dto.CommentResponseDto;
import smu.capstone.domain.comment.repository.CommentRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static smu.capstone.common.errorcode.CommonStatusCode.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    //  댓글 작성
    @Transactional
    public CommentResponseDto addComment(Long boardId, Long userId, CommentRequestDto requestDto) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_USER));

        Comment comment = Comment.builder()
                .content(requestDto.getContent())
                .board(board)
                .user(user)
                .build();

        commentRepository.save(comment);
        return new CommentResponseDto(comment);
    }

    //  특정 게시글의 댓글 조회
    public List<CommentResponseDto> getComments(Long boardId) {
        List<Comment> comments = commentRepository.findByBoardId(boardId);
        return comments.stream()
                .map(CommentResponseDto::new)
                .collect(Collectors.toList());
    }

    //  댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RestApiException(FORBIDDEN);
        }

        commentRepository.delete(comment);
    }
}
