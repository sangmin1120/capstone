package smu.capstone.domain.chatroom.dto;

import lombok.*;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MessageScrollResponseDto {
    private List<ChatMessage> messages;
    private MessageCursor nextCursor;

    @Builder @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageCursor {
        private String lastMessageId;
        private LocalDateTime lastSentAt;
        private boolean hasNext;
    }
}