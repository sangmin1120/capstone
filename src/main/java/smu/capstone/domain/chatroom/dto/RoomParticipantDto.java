package smu.capstone.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smu.capstone.domain.member.entity.UserType;

/***
 * User Entity 정보 제공하는 Dto
 * 비밀번호 등 민감 정보는 제외함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomParticipantDto {
    //id로 조회.
    Long id;                // 조회/삭제/유저확인
    String userId;
    String username;        // 프론트에 보여질 닉네임
    String imgUrl;
    UserType userType;
}
