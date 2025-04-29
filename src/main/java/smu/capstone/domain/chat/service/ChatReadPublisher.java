package smu.capstone.domain.chat.service;

import io.jsonwebtoken.io.SerializationException;
import io.lettuce.core.RedisException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import smu.capstone.common.errorcode.ChatExceptionCode;
import smu.capstone.domain.chat.domain.ChatMessage;
import smu.capstone.domain.chat.dto.ChatMessageDto;
import smu.capstone.domain.chat.exception.ChatException;
import smu.capstone.domain.chatroom.domain.ChatRoomUser;
import smu.capstone.domain.chatroom.repository.ChatRoomUserRepository;
import smu.capstone.domain.member.entity.UserEntity;
import smu.capstone.domain.member.respository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatReadPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final UserRepository userRepository;

    public void sendReadState(ChatMessageDto chatMessageDto){
        //에러 던지기
        if(!chatMessageDto.getMessageType().equals(ChatMessage.MessageType.READ)){
            log.error("메시지 잘못 발신: READ TYPE이어야 하지만 {} TYPE 입니다.", chatMessageDto.getMessageType());
            throw new ChatException(ChatExceptionCode.INVALID_MESSAGE_TYPE);
        }

        //내가 안 읽은 메시지 Count를 0으로 변경 후 DB 업데이트
        UserEntity user = userRepository.findByAccountId(chatMessageDto.getSender()).orElseThrow(
                () -> new ChatException(ChatExceptionCode.USER_NOT_FOUND)
        );
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoom_IdAndUserEntity_Id(
                chatMessageDto.getChatRoomId(), user.getId()).orElseThrow(
                () -> new ChatException(ChatExceptionCode.USER_NOT_FOUND)
        );
        chatRoomUser.setNotReadCount(0);
        chatRoomUserRepository.save(chatRoomUser);

        //상대에게 읽었음을 알림 - 프론트에서 해당 메시지를 읽고 읽음 처리(=1을 UI에서 지움)
        // (웹소켓이므로 반영한 DB를 프론트에게 다시 뿌리기 힘듦, 서버는 DB만 반영하고 읽음 처리는 UI에서 처리)
        try {
            redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageDto);
        }catch (RedisException e) {
            log.error("redis 오류: {}", e.getMessage());
            throw new ChatException(ChatExceptionCode.MESSAGE_SENDING_FAILED);
        }catch (SerializationException e){
            log.error("직렬화 오류: {}", e.getMessage());
            throw new ChatException(ChatExceptionCode.DATA_BIND_ERROR);
        }
    }
}