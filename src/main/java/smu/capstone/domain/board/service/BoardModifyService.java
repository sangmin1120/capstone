package smu.capstone.domain.board.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.repository.BoardRepository;
import smu.capstone.domain.board.dto.BoardRequestDto;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.service.InfoService;

import java.util.Optional;

import static smu.capstone.common.errorcode.CommonStatusCode.*;

@Service
@RequiredArgsConstructor
public class BoardModifyService {

    private final BoardRepository boardRepository;
    private final InfoService infoService;

    @Transactional
    public Board createBoard(BoardRequestDto requestDto) {
        // 작성자 찾기
        UserEntity currentUser = infoService.getCurrentUser();

        // 게시글 생성
        Board board = Board.builder()
                .user(currentUser)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .boardType(requestDto.getBoardType())
                .build();

        return boardRepository.save(board);
    }

    public void updateBoard(Long boardId, BoardRequestDto requestDto) {

        UserEntity currentUser = infoService.getCurrentUser();
        Optional<Board> board = boardRepository.findById(boardId);
        if (!currentUser.equals(board.get().getUser())) {
            throw new RestApiException(NOT_FOUND_BOARD_ID);
        }

        //수정할 부분: 업데이트 부분
        Board updated = board.get().update(requestDto);
        boardRepository.save(updated);
    }

    public void deleteBoard(Long id) {
        UserEntity currentUser = infoService.getCurrentUser();
        Optional<Board> board = boardRepository.findById(id);

        // 게시글의 주인이 아니면 삭제 불가
        if (!board.get().getUser().equals(currentUser)) {
            throw new RestApiException(FORBIDDEN);
        }

        if (board.isPresent()) {
            boardRepository.delete(board.get());
        }
    }
}

