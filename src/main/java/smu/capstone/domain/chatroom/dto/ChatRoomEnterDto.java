package smu.capstone.domain.chatroom.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomEnterDto {
    private String userId;
    private RoomParticipantDto participant;
    private Long otherUserUnreadCount;
    private boolean isOtherOnline; //상대 채팅방 참여 여부
}
