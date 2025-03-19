package smu.capstone.object.comment.dto;

import lombok.Getter;
import smu.capstone.object.comment.domain.Comment;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String username; // 댓글 작성자 이름

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getUser().getUsername();
    }
}
