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
import smu.capstone.domain.chat.exception.ChatException;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatLeavePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void sendLeaveState(String roomId, String username) {
        try {
            ChatMessage leaveMessage = getLeaveState(roomId, username);
            redisTemplate.convertAndSend(channelTopic.getTopic(), leaveMessage);
        } catch (RedisException e) {
            log.error("redis 오류: {}", e.getMessage());
            throw new ChatException(ChatExceptionCode.MESSAGE_SENDING_FAILED);
        } catch (SerializationException e) {
            log.error("직렬화 오류: {}", e.getMessage());
            throw new ChatException(ChatExceptionCode.DATA_BIND_ERROR);
        } catch (Exception e) {
            log.error("기타 에러 {}", e.getMessage());
            throw new ChatException(ChatExceptionCode.MESSAGE_SENDING_FAILED);
        }
    }

    public ChatMessage getLeaveState(String roomId, String username) {
        return ChatMessage.builder().chatRoomId(roomId)
                .messageType(ChatMessage.MessageType.LEAVE)
                .sender(username)
                .sentAt(LocalDateTime.now())
                .message(null)
                .id(null)
                .build();
    }
}
