package smu.capstone.object.like;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{boardId}/like")
    public ResponseEntity<String> likeBoard(@PathVariable("boardId") Long boardId, HttpServletRequest request) {
        String response = likeService.likeBoard(boardId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{boardId}/like")
    public ResponseEntity<String> unlikeBoard(@PathVariable("boardId") Long boardId, HttpServletRequest request) {
        String response = likeService.unlikeBoard(boardId, request);
        return ResponseEntity.ok(response);
    }
}

