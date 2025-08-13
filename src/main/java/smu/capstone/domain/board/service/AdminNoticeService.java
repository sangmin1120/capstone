package smu.capstone.domain.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.board.dto.BoardRequestDto;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.entity.BoardType;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.comment.repository.CommentRepository;
import smu.capstone.domain.like.repository.LikeRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

import static smu.capstone.common.errorcode.CommonStatusCode.INVALID_BOARD_TYPE;
import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_BOARD_ID;

@Service
@RequiredArgsConstructor
public class AdminNoticeService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final InfoService infoService;

    @Transactional
    public Board createNotice(BoardRequestDto requestDto) {
        UserEntity admin = infoService.getCurrentUser();

        Board board = new Board();
        board.setUser(admin);
        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        board.setBoardType(BoardType.NOTICE); // 고정
        board.setImgUrl(requestDto.getImgUrl());

        return boardRepository.save(board);
    }

    @Transactional
    public void updateNotice(Long id, BoardRequestDto requestDto) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));

        if (board.getBoardType() != BoardType.NOTICE) {
            throw new RestApiException(INVALID_BOARD_TYPE);
        }

        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        board.setImgUrl(requestDto.getImgUrl());
        // boardType, price 수정 불가

        boardRepository.save(board);
    }

    @Transactional
    public void deleteNotice(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));

        if (board.getBoardType() != BoardType.NOTICE) {
            throw new RestApiException(INVALID_BOARD_TYPE);
        }

        // 관련 댓글/좋아요 삭제
        commentRepository.deleteByBoard(board);
        likeRepository.deleteByBoard(board);

        boardRepository.delete(board);
    }
}


