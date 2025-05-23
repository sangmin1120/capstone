package smu.capstone.domain.chat.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collation = "chat_messages")
public class ChatMessage {
    @Id
    private String id;
    private String chatRoomId;
    private String message;
    private String sender;  //AccountId로 에러메시지 전송하므로.. AccountId 값을 넣어야 함
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime sentAt;
    private MessageType messageType;

    /***
     * TEXT: 일반 메시지
     * IMAGE: 이미지 url
     * FILE: 이미지 외 FILE url
     * READ: 유저 입장 메시지(읽음 처리)
     */
    public enum MessageType {
        TEXT, IMAGE, FILE, READ, LEAVE
    }
}
