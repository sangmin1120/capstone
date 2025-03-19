package smu.capstone.object.board.dto;

import lombok.Getter;
import lombok.Setter;
import smu.capstone.object.board.domain.BoardType;


@Getter
@Setter
public class BoardRequestDto {

    private String title;
    private String content;
    private BoardType boardType;
    private String imgUrl;
}
