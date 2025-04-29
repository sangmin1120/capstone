package smu.capstone.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;
import smu.capstone.domain.chat.dto.ChatMessageDto;
import smu.capstone.domain.chat.service.ChatReadPublisher;
import smu.capstone.domain.chat.service.RedisPublisher;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final RedisPublisher redisPublisher;
    private final ChatReadPublisher chatReadPublisher;

    @MessageMapping("/sendMessage")
    public void sendMessage(ChatMessageDto chatMessageDto){
        //메시지 발신 - STOMP 메시지를 Redis Publisher(redis)로 발행
        log.info("메시지 발행 -> pub");
        redisPublisher.publish(chatMessageDto);
    }

    @MessageMapping("/sendReadState")
    public void sendReadState(ChatMessageDto chatMessageDto){
        log.info("입장 메시지 발행");
        chatReadPublisher.sendReadState(chatMessageDto);
    }
}
