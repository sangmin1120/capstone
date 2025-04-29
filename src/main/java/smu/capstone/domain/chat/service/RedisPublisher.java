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

import java.time.LocalDateTime;
@Slf4j
@RequiredArgsConstructor
@Component
public class RedisPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final AsyncChatMessageService asyncChatMessageService;

    public void publish(ChatMessageDto chatMessageDto) {

        validateMessage(chatMessageDto);
        ChatMessage chatMessage = getChatMessage(chatMessageDto);
        if(chatMessage == null) {
            throw new ChatException(ChatExceptionCode.DATA_BIND_ERROR);
        }
        //레디스 메시지 전송
        sendMessageToRedis(chatMessage);

        //전송 성공 시에만 저장 시도
        asyncChatMessageService.saveChatMessage(chatMessage);
        asyncChatMessageService.updateChatRoomInfo(chatMessage);
    }

    //만약 UserId로 설정할 경우 - 다른 User정보를 프론트에서 가지고 있어야 함
    private void sendMessageToRedis(ChatMessage chatMessage) {
        try {
            redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
        } catch (RedisException | SerializationException e) {
            log.error("Redis 전송 실패: {}", e.getMessage());
            throw new ChatException(ChatExceptionCode.MESSAGE_SENDING_FAILED);
        }
    }

    private void validateMessage(ChatMessageDto chatMessageDto) {
        //메시지 NULL 확인
        if (chatMessageDto == null || chatMessageDto.getMessage() == null
                || chatMessageDto.getMessage().isEmpty()) {
            log.error("chat message is null or empty");
            throw new ChatException(ChatExceptionCode.NULL_CONTENT);
        }

        //메시지 Type 확인
        if (chatMessageDto.getMessageType()!=null && chatMessageDto.getMessageType().equals(ChatMessage.MessageType.READ)) {
            log.error("Error: received read message");
            throw new ChatException(ChatExceptionCode.INVALID_MESSAGE_TYPE);
        }
    }

    private ChatMessage getChatMessage(ChatMessageDto chatMessageDto) {
        return ChatMessage.builder()
                .chatRoomId(chatMessageDto.getChatRoomId())
                .messageType(chatMessageDto.getMessageType())
                .sender(chatMessageDto.getSender())
                .message(chatMessageDto.getMessage())
                .sentAt(LocalDateTime.now())
                .build();
    }
}