package smu.capstone.domain.chatroom.dto;

import lombok.*;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomEnterDto {
    private String userId;
    private RoomParticipantDto participant;
    private int otherUserUnreadCount;
    private List<ChatMessage> chatMessageList;
}
