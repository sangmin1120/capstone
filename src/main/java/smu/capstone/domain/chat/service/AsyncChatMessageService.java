package smu.capstone.domain.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import smu.capstone.common.errorcode.ChatExceptionCode;
import smu.capstone.domain.chat.domain.ChatMessage;
import smu.capstone.domain.chat.exception.ChatException;
import smu.capstone.domain.chat.repository.ChatMessageRepository;
import smu.capstone.domain.chatroom.domain.ChatRoom;
import smu.capstone.domain.chatroom.domain.ChatRoomUser;
import smu.capstone.domain.chatroom.dto.ChatRoomUserPair;
import smu.capstone.domain.chatroom.repository.ChatRoomRepository;
import smu.capstone.domain.chatroom.repository.ChatRoomUserRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;
import smu.capstone.intrastructure.chatting.util.RedisSessionManager;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class AsyncChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final RedisSessionManager redisSessionManager;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Async
    @Transactional
    public void saveChatMessage(ChatMessage chatMessage) {
        try {
            chatMessageRepository.save(chatMessage);
            log.info("메시지 저장 완료: {}", chatMessage.getMessage());
        } catch (Exception e) {
            log.error("메시지 저장 실패", e);
            throw new ChatException(ChatExceptionCode.MESSAGE_SAVE_FAILED);
        }
    }

    @Async
    @Transactional
    /*메시지 관련 정보 비동기 업데이트 - notReadCnt, Activation 업데이트*/
    public void updateChatRoomInfo(ChatMessage message) {

        // 쿼리 변경?
        ChatRoom chatRoom = chatRoomRepository.findById(message.getChatRoomId()).orElseThrow(
                () -> new ChatException(ChatExceptionCode.ROOM_NOT_EXIST));

        if(redisSessionManager.isAloneInRoom(message.getChatRoomId()) ){
            List<ChatRoomUser> chatRoomUserList = chatRoomUserRepository.findByChatRoom_Id(message.getChatRoomId());
            if (chatRoomUserList == null || chatRoomUserList.isEmpty()) {
                log.error("No chat room user found");
                throw new ChatException(ChatExceptionCode.NO_ONE_PRESENT);
            }

            //쿼리 변경 필요 -> 내부 변환하도록 바꿀 것... 혹은 <> 으로 얻거나
            UserEntity user = userRepository.findByAccountId(message.getSender()).orElseThrow( // 이부분 sender --------------------- AccountId를 넣어줘야됨
                    () -> new ChatException(ChatExceptionCode.USER_NOT_FOUND));
            ChatRoomUser other = ChatRoomUserPair.getPair(user.getId(), chatRoomUserList).getOtherChatRoomUser();

            if(other.getActivation().equals(ChatRoomUser.Activation.INACTIVE)){
                other.setActivation(ChatRoomUser.Activation.ACTIVE);
            }
            other.setNotReadCount(other.getNotReadCount() + 1);

            try {
                chatRoomUserRepository.save(other);
            }catch (Exception e) {
                log.error("error:{}, exception: {}",e.getMessage(), e.getCause());
                throw new ChatException(ChatExceptionCode.MESSAGE_SAVE_FAILED);
            }
        }
        chatRoom.setLastMessageAt(message.getSentAt());

        try {
            chatRoomRepository.save(chatRoom);
        }catch (Exception e){
            log.error("error:{}, exception: {}",e.getMessage(), e.getCause());
            throw new ChatException(ChatExceptionCode.MESSAGE_SAVE_FAILED);
        }
    }
}
