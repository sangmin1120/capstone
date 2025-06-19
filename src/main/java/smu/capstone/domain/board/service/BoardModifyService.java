package smu.capstone.domain.board.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.entity.BoardType;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.board.dto.BoardRequestDto;
import smu.capstone.domain.comment.repository.CommentRepository;
import smu.capstone.domain.like.repository.LikeRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

import static smu.capstone.common.errorcode.CommonStatusCode.*;

@Service
@RequiredArgsConstructor
public class BoardModifyService {

    private final BoardRepository boardRepository;
    private final InfoService infoService;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Board createBoard(BoardRequestDto requestDto) {
        // 작성자 찾기
        UserEntity currentUser = infoService.getCurrentUser();

        // 게시글 생성
        Board board = new Board();
        board.setUser(currentUser);
        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        board.setBoardType(requestDto.getBoardType());
        board.setImgUrl(requestDto.getImgUrl());

        // BoardType이 MARKET이면 price 설정
        if (requestDto.getBoardType() == BoardType.MARKET) {
            board.setPrice(requestDto.getPrice());
        }

        return boardRepository.save(board);
    }

    public void updateBoard(Long boardId, BoardRequestDto requestDto) {
        UserEntity currentUser = infoService.getCurrentUser();
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));

        if (!currentUser.equals(board.getUser())) {
            throw new RestApiException(FORBIDDEN);
        }

        board.setTitle(requestDto.getTitle());
        board.setContent(requestDto.getContent());
        board.setBoardType(requestDto.getBoardType());
        board.setImgUrl(requestDto.getImgUrl());

        if (requestDto.getBoardType() == BoardType.MARKET) {
            board.setPrice(requestDto.getPrice());
        } else {
            board.setPrice(null); // 다른 게시판 유형일 경우 price 초기화
        }

        boardRepository.save(board);
    }


    @Transactional
    public void deleteBoard(Long id) {
        UserEntity currentUser = infoService.getCurrentUser();
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RestApiException(NOT_FOUND_BOARD_ID));

        // 작성자 확인
        if (!board.getUser().equals(currentUser)) {
            throw new RestApiException(FORBIDDEN);
        }
        // 나중에 수정할 필요 있음
        // 1. 관련 댓글 삭제
        commentRepository.deleteByBoard(board);
        // 2. 관련 좋아요 삭제
        likeRepository.deleteByBoard(board);
        // 3. 게시글 삭제
        boardRepository.delete(board);
    }

}

