package smu.capstone.domain.chatroom.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
/***
 * 순서를 꼭 지킬 것.
 * 1번째 userId: 생성 요청한 userId
 * 2번째 otherUserId: 채팅방에 참여할 다른 user Id
 */
public class ChatRoomCreateDto {
    private Long userId;
    private Long otherUserId;
}
