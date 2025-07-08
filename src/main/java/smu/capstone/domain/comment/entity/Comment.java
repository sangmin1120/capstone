package smu.capstone.domain.comment.entity;

import jakarta.persistence.*;
import lombok.*;
import smu.capstone.domain.board.dto.BoardRequestDto;
import smu.capstone.domain.board.entity.Board;
import smu.capstone.domain.member.entity.UserEntity;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;  // 원본 게시글

    private String content;  // 댓글 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;  // 댓글 작성자

    public void update(String content) {
        this.content = content;
    }

}
