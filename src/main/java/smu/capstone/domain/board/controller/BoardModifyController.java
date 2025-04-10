package smu.capstone.domain.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.board.service.BoardModifyService;
import smu.capstone.domain.board.dto.BoardRequestDto;
import smu.capstone.domain.board.dto.BoardResponseDto;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardModifyController {

    private final BoardModifyService boardModifyService;

    @PostMapping
    public BaseResponse<BoardResponseDto> createBoard(@RequestBody BoardRequestDto requestDto) {
        Board board = boardModifyService.createBoard(requestDto);
        return BaseResponse.ok(new BoardResponseDto(board));
    }

    @PutMapping("/{boardId}")
    public BaseResponse<Void> updateBoard(@PathVariable("boardId") Long boardId, @RequestBody BoardRequestDto requestDto, HttpServletRequest request) {
        boardModifyService.updateBoard(boardId, requestDto);
        return BaseResponse.ok();
    }

    @DeleteMapping("/{boardId}")
    public BaseResponse<Void> deleteBoard(@PathVariable("boardId") Long boardId) {
        boardModifyService.deleteBoard(boardId);
        return BaseResponse.ok();
    }

}
