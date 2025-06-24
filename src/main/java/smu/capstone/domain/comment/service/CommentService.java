package smu.capstone.domain.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.alarm.service.AlarmService;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.comment.entity.Comment;
import smu.capstone.domain.comment.dto.CommentRequestDto;
import smu.capstone.domain.comment.dto.CommentResponseDto;
import smu.capstone.domain.comment.repository.CommentRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.intrastructure.fcm.dto.MessageNotification;
import smu.capstone.intrastructure.fcm.dto.NotificationMulticastRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static smu.capstone.common.errorcode.CommonStatusCode.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final AlarmService alarmService;

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

        // 댓글 저장
        commentRepository.save(comment);
        // 다른 사람들에게 알림
        List<String> fcmTokens = getFcmTokensByBoardId(boardId, user);

        // 4. 알림 발송
        if (!fcmTokens.isEmpty()) {
            String title = "댓글 알림";
            String body = "새로운 댓글: " + requestDto.getContent();
            alarmService.sendMessages(NotificationMulticastRequest.of(fcmTokens, title, body));
        } else {
            log.info("FCM 알림 대상이 없습니다. 댓글만 저장됨.");
        }

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

    public List<String> getFcmTokensByBoardId(Long boardId, UserEntity author) {
        Set<String> fcmTokens = new HashSet<>();

        // 게시글 작성자
        UserEntity boardAuthor = boardRepository.findById(boardId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID))
                .getUser();

        // todo: 수정해야됨 -> 게시글 작성자한테는 알람이 안감
        if (!boardAuthor.equals(author)) {
            String token = boardAuthor.getFcmToken();
            if (token != null && !token.isEmpty()) {
                fcmTokens.add(token);
            }
        }

        // 댓글 작성자 중 방금 작성한 사람 제외
        commentRepository.findByBoardId(boardId).forEach(comment -> {
            UserEntity commenter = comment.getUser();
            if (!commenter.equals(author)) {
                String token = commenter.getFcmToken();
                if (token != null && !token.isEmpty()) {
                    fcmTokens.add(token);
                }
            }
        });

        return new ArrayList<>(fcmTokens);
    }

    // 댓글 수정
    @Transactional
    public void updateComment(Long commentId, Long userId, CommentRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new RestApiException(FORBIDDEN);
        }

        comment.update(requestDto.getContent());
    }
}
