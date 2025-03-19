package smu.capstone.object.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.object.board.domain.Board;
import smu.capstone.object.board.service.BoardModifyService;
import smu.capstone.object.board.dto.BoardRequestDto;
import smu.capstone.object.board.dto.BoardResponseDto;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardModifyController {

    private final BoardModifyService boardModifyService;

    @PostMapping
    public BaseResponse<BoardResponseDto> createBoard(@RequestBody BoardRequestDto requestDto, HttpServletRequest request) {
        Board board = boardModifyService.createBoard(requestDto, request);
        return BaseResponse.ok(new BoardResponseDto(board));
    }

    @PutMapping("/{boardId}")
    public BaseResponse<Void> updateBoard(@PathVariable("boardId") Long boardId, @RequestBody BoardRequestDto requestDto, HttpServletRequest request) {
        boardModifyService.updateBoard(boardId, requestDto, request);
        return BaseResponse.ok();
    }

    @DeleteMapping("/{boardId}")
    public BaseResponse<Void> deleteBoard(@PathVariable("boardId") Long boardId, HttpServletRequest request) {
        boardModifyService.deleteBoard(boardId, request);
        return BaseResponse.ok();
    }

}
