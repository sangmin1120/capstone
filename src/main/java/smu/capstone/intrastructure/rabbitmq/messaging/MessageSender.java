package smu.capstone.intrastructure.rabbitmq.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smu.capstone.common.util.CertificationKeyGenerator;
import smu.capstone.intrastructure.mail.dto.EmailType;
import smu.capstone.intrastructure.rabbitmq.dto.AlarmMessageDto;
import smu.capstone.intrastructure.rabbitmq.dto.AuthMessageDto;
import smu.capstone.intrastructure.rabbitmq.dto.MessageDto;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageSender {

    @Value("${spring.rabbitmq.bindings[0].exchange.name}")
    private String auth_exchangeName;
    @Value("${spring.rabbitmq.bindings[1].exchange.name}")
    private String reset_exchangeName;
    @Value("${spring.rabbitmq.bindings[2].exchange.name}")
    private String alarm_exchangeName;

    private final RabbitTemplate rabbitTemplate;

    // auth-mail
    public void sendMessage(String email, EmailType type, String key) {
        AuthMessageDto messageDto = new AuthMessageDto(email, type, key); // 구분 해야됨

        String routingKey = getRoutingKeyForEmailType(type);
        rabbitTemplate.convertAndSend(auth_exchangeName, routingKey, messageDto);
    }

    // schedule-mail
    public void sendMessage(String email, EmailType type, Map<String, String> payload) {
        AlarmMessageDto alarmMessageDto = new AlarmMessageDto(email, type, payload);

        String routingKey = getRoutingKeyForEmailType(type);
        rabbitTemplate.convertAndSend(alarm_exchangeName, routingKey, alarmMessageDto);
    }


    private String getRoutingKeyForEmailType(EmailType type) {

        return switch (type) {
            case SIGNUP_CODE_MAIL, PASSWORD_CODE_MAIL -> "email.auth";
            case PASSWORD_RESET -> "email.reset-password";
            case SCHEDULE_ALARM -> "email.schedule-alarm";
        };
    }

}
