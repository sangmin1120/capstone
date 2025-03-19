package smu.capstone.object.board.dto;

import lombok.Getter;
import lombok.Setter;
import smu.capstone.object.board.domain.Board;
import smu.capstone.object.member.domain.UserEntity;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardResponseDto {
    private Long boardId;
    private String title;
    private String content;
    private String boardType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private String email;
    private String imgUrl;
    private int likeCount;

    public BoardResponseDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.boardType = board.getBoardType().toString();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
        UserEntity user = board.getUser();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.imgUrl= user.getImgUrl();
        this.likeCount=board.getLikeCount();
    }
}

