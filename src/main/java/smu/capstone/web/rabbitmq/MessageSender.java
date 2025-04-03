package smu.capstone.web.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static smu.capstone.object.member.service.EmailService.*;

@Service
@RequiredArgsConstructor
public class MessageSender {

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String email, EmailType type) {
        MessageDto messageDto = new MessageDto(email, type);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, messageDto);
    }
}
