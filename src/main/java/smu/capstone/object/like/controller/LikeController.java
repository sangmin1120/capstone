package smu.capstone.object.like.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.object.like.service.LikeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{boardId}/like")
    public BaseResponse<String> likeBoard(@PathVariable("boardId") Long boardId, HttpServletRequest request) {
        String response = likeService.likeBoard(boardId, request);
        return BaseResponse.ok(response);
    }

    @DeleteMapping("/{boardId}/like")
    public BaseResponse<String> unlikeBoard(@PathVariable("boardId") Long boardId, HttpServletRequest request) {
        String response = likeService.unlikeBoard(boardId, request);
        return BaseResponse.ok(response);
    }
}

