package smu.capstone.domain.like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.domain.like.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{boardId}/like")
    public BaseResponse<String> likeBoard(@PathVariable("boardId") Long boardId) {
        String response = likeService.likeBoard(boardId);
        return BaseResponse.ok(response);
    }

    @DeleteMapping("/{boardId}/like")
    public BaseResponse<String> unlikeBoard(@PathVariable("boardId") Long boardId) {
        String response = likeService.unlikeBoard(boardId);
        return BaseResponse.ok(response);
    }
}

