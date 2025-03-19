package smu.capstone.object.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.object.board.domain.BoardType;
import smu.capstone.object.board.dto.BoardResponseDto;
import smu.capstone.object.board.service.BoardSearchService;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardSearchController {

    private final BoardSearchService boardSearchService;

    @GetMapping
    public BaseResponse<List<BoardResponseDto>> getAllBoards() {
        return BaseResponse.ok(boardSearchService.findAllBoards());
    }

    @GetMapping("/type/{boardType}")
    public BaseResponse<List<BoardResponseDto>> getBoardsByType(@PathVariable("boardType") String boardType) {
        BoardType type = BoardType.valueOf(boardType.toUpperCase()); // ENUM 변환
        return BaseResponse.ok(boardSearchService.findBoardsByType(type));
    }

    @GetMapping("/{boardId}")
    public BaseResponse<BoardResponseDto> getBoardById(@PathVariable("boardId") Long boardId) {
        return BaseResponse.ok(boardSearchService.findBoardById(boardId));
    }

    @GetMapping("/search")
    public BaseResponse<List<BoardResponseDto>> searchBoards(@RequestParam("keyword") String keyword) {
        return BaseResponse.ok(boardSearchService.searchBoards(keyword));
    }
}

