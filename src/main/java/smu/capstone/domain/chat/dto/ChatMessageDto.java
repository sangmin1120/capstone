package smu.capstone.domain.chat.dto;

import lombok.*;
import smu.capstone.domain.chat.domain.ChatMessage;

import java.time.LocalDateTime;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String chatRoomId;
    private String message;
    //프론트에서 설정할 것인지에 대한 여부 확인하기.
    private LocalDateTime timestamp;
    private String sender;
    private ChatMessage.MessageType messageType;
}
