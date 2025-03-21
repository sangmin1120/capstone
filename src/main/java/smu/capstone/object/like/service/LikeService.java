package smu.capstone.object.like.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.board.domain.Board;
import smu.capstone.object.board.repository.BoardRepository;
import smu.capstone.object.like.domain.Like;
import smu.capstone.object.like.repository.LikeRepository;
import smu.capstone.object.member.domain.UserEntity;
import smu.capstone.object.member.service.InfoService;


import java.util.Optional;

import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_BOARD_ID;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final InfoService infoService;


    @Transactional
    public String likeBoard(Long boardId, HttpServletRequest request) {
        //  게시글 및 사용자 찾기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));
        UserEntity user = infoService.getCurrentUser(request);

        // 사용자가 이미 좋아요를 눌렀는지 확인
        if (likeRepository.existsByUserAndBoard(user, board)) {
            return "이미 좋아요를 눌렀습니다.";
        }

        // 좋아요 추가
        Like like = Like.builder()
                .user(user)
                .board(board)
                .build();

        likeRepository.save(like);
        board.increaseLike();  // 좋아요 수 증가
        boardRepository.save(board);
        return "게시글에 좋아요를 눌렀습니다.";
    }

    @Transactional
    public String unlikeBoard(Long boardId, HttpServletRequest request) {
        // 게시글 및 사용자 찾기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));
        UserEntity user = infoService.getCurrentUser(request);

        // 좋아요 존재 여부 확인 후 삭제
        Optional<Like> like = likeRepository.findByUserAndBoard(user, board);
        if (like.isPresent()) {
            likeRepository.delete(like.get());
            board.decreaseLike();  // 좋아요 수 감소
            boardRepository.save(board);
            return "게시글의 좋아요를 취소했습니다.";
        } else {
            return "좋아요를 누르지 않은 게시글입니다.";
        }
    }
}



