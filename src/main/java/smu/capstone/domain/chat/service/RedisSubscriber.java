package smu.capstone.domain.chat.service;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import smu.capstone.common.errorcode.ChatExceptionCode;
import smu.capstone.domain.chat.domain.ChatMessage;
import smu.capstone.domain.chat.exception.ChatException;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try{
            //역직렬화 STOMP로 보냄
            ChatMessage chatMessage = objectMapper.readValue(message.getBody(), ChatMessage.class);
            messagingTemplate.convertAndSend("/sub/chat/"+chatMessage.getChatRoomId(), chatMessage);
        }
        catch (ChatException e){
            throw new ChatException(ChatExceptionCode.MESSAGE_SENDING_FAILED);
        }
        catch (DatabindException e){
            log.error("DatabindException in RedisSubscriber: 메시지 형식을 다시 확인해주세요");
            throw new ChatException(ChatExceptionCode.DATA_BIND_ERROR);
        }
        catch (IOException e){
            log.error("IOException in RedisSubscriber");
            throw new ChatException(ChatExceptionCode.IO_ERROR);
        }
    }
}
