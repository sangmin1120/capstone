package smu.capstone.domain.board.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.board.dto.BoardResponseDto;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.entity.BoardType;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.comment.repository.CommentRepository;
import smu.capstone.domain.like.repository.LikeRepository;

import java.util.List;
import java.util.stream.Collectors;

import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_BOARD_ID;
import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_BOARD_TYPE;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminBoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    // 게시글 강제 삭제 (작성자 상관없이)
    public void adminDeleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));

        commentRepository.deleteByBoard(board);
        likeRepository.deleteByBoard(board);
        boardRepository.delete(board);
    }

}

