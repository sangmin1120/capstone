package smu.capstone.object.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.object.board.domain.Board;
import smu.capstone.object.board.repository.BoardRepository;
import smu.capstone.object.comment.domain.Comment;
import smu.capstone.object.comment.dto.CommentRequestDto;
import smu.capstone.object.comment.dto.CommentResponseDto;
import smu.capstone.object.comment.repository.CommentRepository;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.respository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if (!comment.getUser().getId().equals(userId)) {
            throw new SecurityException("댓글 삭제 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
