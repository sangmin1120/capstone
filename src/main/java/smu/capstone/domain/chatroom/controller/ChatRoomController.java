package smu.capstone.domain.chatroom.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.errorcode.ChatRoomExceptionCode;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.common.response.ChatRoomResponse;
import smu.capstone.domain.chatroom.dto.ChatRoomCreateDto;
import smu.capstone.domain.chatroom.dto.ChatRoomDto;
import smu.capstone.domain.chatroom.dto.ChatRoomEnterDto;
import smu.capstone.domain.chatroom.service.ChatRoomService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms")
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //채팅방 조회
    //userId의 경우 UserEntity의 id로 구현, userId(String)가 아님 주의
    @GetMapping("/{userId}")
    public BaseResponse<List<ChatRoomDto>> getChatRoomList(@PathVariable("userId") Long userId) {
        List<ChatRoomDto> chatRoomList = chatRoomService.getChatRoomList(userId);
        return BaseResponse.ok(chatRoomList);
    }

    //채팅방 생성
    @PostMapping("/create")
    public BaseResponse<String> createChatRoom(@RequestBody ChatRoomCreateDto createDto) {
        String roomId = chatRoomService.createChatRoom(createDto);
        return BaseResponse.ok(roomId);
    }

    //채팅방 입장
    @GetMapping("/{userId}/enter/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse<ChatRoomEnterDto>> enterChatRoom(@PathVariable("userId") Long userId, @PathVariable("chatRoomId") String chatRoomId) {
        //상대 사용자가 탈퇴 회원인지 확인
        ChatRoomEnterDto chatRoomEnterDto = chatRoomService.enterChatRoom(chatRoomId, userId);

        boolean isDeleteUser = chatRoomService.isDeleteUserId(chatRoomId, userId);
        if(isDeleteUser) {
            //메시지는 반환, 상대가 없다는 것을 알림.
            return ChatRoomResponse.ok(ChatRoomExceptionCode.NOT_FOUND_USER, chatRoomEnterDto);
        }
        return ChatRoomResponse.ok(CommonStatusCode.OK, chatRoomEnterDto);
    }

    //채팅방 삭제
    @DeleteMapping("/{userId}/delete/{chatRoomId}")
    public BaseResponse deleteChatRoom(@PathVariable("userId") Long userId, @PathVariable("chatRoomId") String chatRoomId) {
        chatRoomService.deleteChatRoom(userId, chatRoomId);
        return BaseResponse.ok();
    }
}
