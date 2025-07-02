package smu.capstone.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import smu.capstone.common.errorcode.ChatExceptionCode;
import smu.capstone.domain.alarm.service.AlarmService;
import smu.capstone.domain.chat.domain.ChatMessage;
import smu.capstone.domain.chat.exception.ChatException;
import smu.capstone.domain.chat.repository.ChatMessageRepository;
import smu.capstone.domain.chatroom.domain.ChatRoom;
import smu.capstone.domain.chatroom.domain.ChatRoomUser;
import smu.capstone.domain.chatroom.repository.ChatRoomRepository;
import smu.capstone.domain.chatroom.repository.ChatRoomUserRepository;
import smu.capstone.intrastructure.chatting.util.RedisSessionManager;
import smu.capstone.intrastructure.fcm.dto.MessageNotification;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final RedisSessionManager redisSessionManager;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    //private final UserRepository userRepository;
    private final AlarmService alarmService;

    @Async
    public void saveChatMessage(ChatMessage chatMessage) {
        try {
            chatMessageRepository.save(chatMessage);
            log.info("메시지 저장 완료: {}", chatMessage.getMessage());
            //throw new Exception("메시지 일부러 실패해봄");
        } catch (Exception e) {
            log.error("메시지 저장 실패", e);
            throw new ChatException(ChatExceptionCode.MESSAGE_SAVE_FAILED);
        }
    }

    @Async
    /*메시지 관련 정보 비동기 업데이트 - notReadCnt, Activation 업데이트*/
    public void updateChatRoomInfo(ChatMessage message) {

        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatRoomId()).orElseThrow(
                () -> new ChatException(ChatExceptionCode.ROOM_NOT_EXIST));

        //현재 채팅 세션에 참여인원이 1명(본인)이면 채팅방 업데이트/메시지 업데이트/알림메시지전송 진행
        if(redisSessionManager.isAloneInRoom(message.getChatRoomId()) ){
//            List<ChatRoomUser> chatRoomUserList = chatRoomUserRepository.findByChatRoom_Id(message.getChatRoomId());
//            if (chatRoomUserList == null || chatRoomUserList.isEmpty()) {
//                log.error("No chat room user found");
//                throw new ChatException(ChatExceptionCode.NO_ONE_PRESENT);
//            }

            //쿼리 변경 필요 -> 내부 변환하도록 바꿀 것... 혹은 <> 으로 얻거나
//            UserEntity user = userRepository.findByAccountId(message.getSender()).orElseThrow( // 이부분 sender --------------------- AccountId를 넣어줘야됨
//                    () -> new ChatException(ChatExceptionCode.USER_NOT_FOUND));
//            ChatRoomUser other = ChatRoomUserPair.getPair(user.getId(), chatRoomUserList).getOtherChatRoomUser();
            //test
//            if(true){
//                throw new ChatException(ChatExceptionCode.MESSAGE_SENDING_FAILED);
//            }
            ChatRoomUser other = chatRoomUserRepository.
                    findOtherChatRoomUserByChatRoom_idAndUserEntity_AccountId(message.getChatRoomId(),
                            message.getSender()).orElseGet( () ->
                            {
                                log.warn("[AsyncChatMessageService::updateChatRoomInfo]: 상대 사용자를 찾을 수 없습니다. roomId: {}, user: {}",
                                        message.getChatRoomId(), message.getSender());
                                return null;
                            });
            //상대 사용자가 없다면 에러메시지 전송
            if(other == null || other.getUserEntity() == null){
                throw new ChatException(ChatExceptionCode.USER_NOT_FOUND);
            }

            //상대 채팅방이 비활성화 상태라면 활성화
            if(other.getActivation().equals(ChatRoomUser.Activation.INACTIVE)){
                other.setActivation(ChatRoomUser.Activation.ACTIVE);
            }

            other.setNotReadCount(other.getNotReadCount() + 1);

            try {
                chatRoomUserRepository.save(other);
            }catch (Exception e) {
                log.error("error:{}, exception: {}", e.getMessage(), e.getCause().toString(), e);
                throw new ChatException(ChatExceptionCode.MESSAGE_SAVE_FAILED);
            }
            //모두 저장한 후 메시지 전송
            //토큰 얻음
            String alramToken = other.getUserEntity().getFcmToken();
            //알림 메시지 생성 후 전송
            if(alramToken != null){
                alarmService.sendMessage(MessageNotification.of(alramToken,
                        "새 채팅", message.getSender()+"님이 보낸 채팅입니다."));
            }
        }

        //채팅방 정보 업데이트
        chatRoom.setLastMessageAt(message.getSentAt());
        try {
            chatRoomRepository.save(chatRoom);
        }catch (Exception e){
            log.error("error:{}, exception: {}", e.getMessage(), e.getCause().toString(), e);
            throw new ChatException(ChatExceptionCode.MESSAGE_SAVE_FAILED);
        }
    }
}