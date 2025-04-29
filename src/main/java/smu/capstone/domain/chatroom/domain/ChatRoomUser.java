package smu.capstone.domain.chatroom.domain;

import jakarta.persistence.*;
import lombok.*;
import smu.capstone.domain.member.entity.UserEntity;

import java.time.LocalDateTime;

@Getter @Setter //데이터 수정이 필요하므로 설정
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_room_user", indexes = {
        @Index(name = "idx_user_chatroom", columnList = "user_id, chat_room_id"),
        @Index(name = "idx_chatroom_user", columnList = "chat_room_id, user_id")
})
public class ChatRoomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    //해당 값은 Long으로 설정

    private int notReadCount;           //해당 user가 안 읽은 메시지 수(상대 user X)
    private LocalDateTime createdAt;    //chatRoom 생성 날짜
    @Enumerated(EnumType.STRING)
    private Activation activation;

    //UserEntity의 userid가 기준이 아닌 id(Long)을 기준으로 매핑
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public enum Activation{
        ACTIVE, INACTIVE
    }

    @Builder
    ChatRoomUser(UserEntity userEntity, ChatRoom chatRoom) {
        this.notReadCount = 0;
        this.createdAt = LocalDateTime.now();
        this.activation = Activation.ACTIVE;

        this.userEntity = userEntity;
        this.chatRoom = chatRoom;
    }
}
