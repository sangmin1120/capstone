package smu.capstone.domain.member.util;

import lombok.Getter;

@Getter
public class UserDeleteEvent {
    private Long userId;
    public UserDeleteEvent(Long userId) {
        this.userId = userId;
    }
}
