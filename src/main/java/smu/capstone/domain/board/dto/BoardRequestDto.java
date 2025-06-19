package smu.capstone.domain.board.dto;

import lombok.Getter;
import lombok.Setter;
import smu.capstone.domain.board.entity.BoardType;


@Getter
@Setter
public class BoardRequestDto {

    private String title;
    private String content;
    private BoardType boardType;
    private String imgUrl;
    private Long price;
}
