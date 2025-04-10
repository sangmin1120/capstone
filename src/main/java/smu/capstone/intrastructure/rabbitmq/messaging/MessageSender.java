package smu.capstone.intrastructure.rabbitmq.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smu.capstone.intrastructure.rabbitmq.dto.MessageDto;

import static smu.capstone.domain.member.service.EmailService.*;

@Service
@RequiredArgsConstructor
public class MessageSender {

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String email, EmailType type) {
        MessageDto messageDto = new MessageDto(email, type);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, messageDto);
    }
}
