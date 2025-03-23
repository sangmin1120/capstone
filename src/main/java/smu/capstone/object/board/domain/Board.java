package smu.capstone.object.board.domain;

import jakarta.persistence.*;
import lombok.*;
import smu.capstone.object.board.dto.BoardRequestDto;
import smu.capstone.common.domain.BaseEntity;
import smu.capstone.object.member.domain.UserEntity;

@Entity
@Table(name = "boards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board extends BaseEntity {
    // 게시판, 매칭, 중고 거래, 자유
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user; // 게시글 작성자 (User와 관계)

    @Column(nullable = false)
    private String title; // 게시글 제목

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 게시글 내용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardType boardType; // 게시판 종류 (MATCHING, MARKET, FREE)

    private String imgUrl;

    private int likeCount = 0; // 좋아요 수 추가

    public void increaseLike() {
        this.likeCount++;
    }

    public void decreaseLike() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public Board update(BoardRequestDto dto) {
         this.title = dto.getTitle();
         this.content = dto.getContent();
         this.boardType = dto.getBoardType();
         this.imgUrl = dto.getImgUrl();
         return this;
    }
}

