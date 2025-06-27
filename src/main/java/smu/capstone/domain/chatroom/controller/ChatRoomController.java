package smu.capstone.domain.chatroom.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import smu.capstone.common.errorcode.CommonStatusCode;
import smu.capstone.common.response.BaseResponse;
import smu.capstone.common.response.ChatRoomResponse;
import smu.capstone.domain.chatroom.dto.ChatRoomCreateDto;
import smu.capstone.domain.chatroom.dto.ChatRoomDto;
import smu.capstone.domain.chatroom.dto.ChatRoomEnterDto;
import smu.capstone.domain.chatroom.dto.MessageScrollResponseDto;
import smu.capstone.domain.chatroom.service.ChatRoomService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/chatrooms")
@RestController
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    //채팅방 조회
    @GetMapping("/list")
    public BaseResponse<List<ChatRoomDto>> getChatRoomList() {
        List<ChatRoomDto> chatRoomList = chatRoomService.getChatRoomList();
        return BaseResponse.ok(chatRoomList);
    }

    //채팅방 생성
    @PostMapping("/create")
    public BaseResponse<String> createChatRoom(@RequestBody ChatRoomCreateDto createDto) {
        String roomId = chatRoomService.createChatRoom(createDto);
        return BaseResponse.ok(roomId);
    }

    //채팅방 입장
    @GetMapping("/enter/{chatRoomId}")
    public ResponseEntity<ChatRoomResponse<ChatRoomEnterDto>> enterChatRoom(@PathVariable("chatRoomId") String chatRoomId) {
        //상대가 존재하지 않으면 null 값으로 반환
        ChatRoomEnterDto chatRoomEnterDto = chatRoomService.enterChatRoom(chatRoomId);
        return ChatRoomResponse.ok(CommonStatusCode.OK, chatRoomEnterDto);
    }

    //메시지 페이징
    @GetMapping("/history/{chatRoomId}")
    public BaseResponse<MessageScrollResponseDto> messageHistory(@PathVariable("chatRoomId") String chatRoomId,
                                                                 @RequestParam(required = false) String lastMessageId,
                                                                 @RequestParam(required = false) String lastSentAt,
                                                                 @RequestParam(defaultValue = "20") int size) {
        return BaseResponse.ok(chatRoomService.getMessageHistory(chatRoomId, lastMessageId, lastSentAt, size));
    }

    //채팅방 삭제
    @DeleteMapping("/delete/{chatRoomId}")
    public BaseResponse deleteChatRoom(@PathVariable("chatRoomId") String chatRoomId) {
        chatRoomService.deleteChatRoom(chatRoomId);
        return BaseResponse.ok();
    }
}
