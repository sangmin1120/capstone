package smu.capstone.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/***
 * Room 정보 반환하는 Dto
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomDto {
    private String roomId;                      //RoomId
    private LocalDateTime lastMessageAt;        //해당 값을 기준으로 리스트 갱신
    private Long userId;
    private int notReadCount;                   //해당 user가 안 읽은 메시지 수(상대 user X)
    private List<RoomParticipantDto> participants; //같은 방에 참가하는 다른 USER
}
