package smu.capstone.object.board.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.stereotype.Service;
import smu.capstone.common.exception.RestApiException;
import smu.capstone.object.board.domain.Board;
import smu.capstone.object.board.domain.BoardType;
import smu.capstone.object.board.dto.BoardResponseDto;
import smu.capstone.object.board.repository.BoardRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_BOARD_ID;
import static smu.capstone.common.errorcode.CommonStatusCode.NOT_FOUND_BOARD_TYPE;

@Service
@RequiredArgsConstructor
public class BoardSearchService {

    private final BoardRepository boardRepository;

    public List<BoardResponseDto> findAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<BoardResponseDto> findBoardsByType(BoardType boardType) {
        if (!EnumUtils.isValidEnum(BoardType.class, boardType.name())) {
            throw new RestApiException(NOT_FOUND_BOARD_TYPE);
        }
        List<Board> boards = boardRepository.findByBoardType(boardType);
        return boards.stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }

    public BoardResponseDto findBoardById(Long boardId) {
        Optional<Board> board = boardRepository.findById(boardId);
        if (!board.isPresent()) {
            throw new RestApiException(NOT_FOUND_BOARD_ID);
        }
        return new BoardResponseDto(board.get());
    }

    public List<BoardResponseDto> searchBoards(String keyword) {
        List<Board> searchResults = boardRepository.findByTitleContainingOrContentContaining(keyword, keyword);

        return searchResults.stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
    }
}
