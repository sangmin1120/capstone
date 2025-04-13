package smu.capstone.intrastructure.rabbitmq.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smu.capstone.common.util.CertificationKeyGenerator;
import smu.capstone.intrastructure.mail.dto.EmailType;
import smu.capstone.intrastructure.rabbitmq.dto.MessageDto;

@Service
@RequiredArgsConstructor
public class MessageSender {

    @Value("${spring.rabbitmq.bindings[0].exchange.name}")
    private String exchangeName;

    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(String email, EmailType type) {
        MessageDto messageDto = new MessageDto(email, type, CertificationKeyGenerator.generateBasicKey()); // 구분 해야됨

        String routingKey = getRoutingKeyForEmailType(type);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, messageDto);
    }

    public void sendMessage(String email, EmailType type, String key) {
        MessageDto messageDto = new MessageDto(email, type, key); // 구분 해야됨

        String routingKey = getRoutingKeyForEmailType(type);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, messageDto);
    }

    private String getRoutingKeyForEmailType(EmailType type) {

        return switch (type) {
            case SIGNUP_CODE_MAIL, PASSWORD_CODE_MAIL -> "email.auth";
            case PASSWORD_RESET -> "email.reset-password";
        };
    }
}
