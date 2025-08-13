package smu.capstone.domain.comment.dto;

import lombok.Getter;
import smu.capstone.domain.comment.entity.Comment;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String username; // 댓글 작성자 이름
    private String profileUrl; //댓글 작성자 프로필
    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.username = comment.getUser().getUsername();
        this.profileUrl = comment.getUser().getImgUrl();
    }
}
