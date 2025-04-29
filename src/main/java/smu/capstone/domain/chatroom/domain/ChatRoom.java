package smu.capstone.domain.chatroom.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "chatrooms")
public class ChatRoom {
    @Id
    private String id;  //UUID.ramdomUUID().toString()으로 생성
    private LocalDateTime lastMessageAt;    //해당 값을 기준으로 리스트 갱신
    @OneToMany(mappedBy = "chatRoom")
    private List<ChatRoomUser> chatRoomUsers;

    @Builder
    private ChatRoom(String id, LocalDateTime lastMessageAt, List<ChatRoomUser> chatRoomUsers) {
        this.id = id;
        this.lastMessageAt = lastMessageAt;
        this.chatRoomUsers = chatRoomUsers;
    }
}
