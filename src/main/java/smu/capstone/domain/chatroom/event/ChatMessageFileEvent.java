package smu.capstone.domain.chatroom.event;

import lombok.Getter;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.util.List;

@Getter
public class ChatMessageFileEvent {

    private String roomId;

    public ChatMessageFileEvent(String roomId) {
        this.roomId = roomId;
    }
}
