package smu.capstone.domain.board.event;

import lombok.Getter;

@Getter
public class BoardImageEvent {
    private String beforeImgUrl;
    private String afterImgUrl;

    public BoardImageEvent(String beforeImgUrl, String afterImgUrl) {
        this.beforeImgUrl = beforeImgUrl;
        this.afterImgUrl = afterImgUrl;
    }
}
