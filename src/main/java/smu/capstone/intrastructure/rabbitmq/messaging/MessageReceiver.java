package smu.capstone.intrastructure.rabbitmq.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import smu.capstone.domain.schedule.service.ScheduleMailService;
import smu.capstone.intrastructure.rabbitmq.dto.AlarmMessageDto;
import smu.capstone.intrastructure.rabbitmq.dto.AuthMessageDto;
import smu.capstone.intrastructure.rabbitmq.dto.MessageDto;
import smu.capstone.intrastructure.mail.service.EmailService;
import smu.capstone.intrastructure.redis.domain.MailVerificationCache;
import smu.capstone.intrastructure.redis.repository.MailVerificationCacheRepository;

@Component
@RequiredArgsConstructor
public class MessageReceiver {

    private static final Long CERTIFICATION_KEY_EXPIRE_SECONDS = 10 * 60L;

    private final EmailService emailService;
    private final ScheduleMailService scheduleMailService;
    private final MailVerificationCacheRepository mailVerificationCacheRepository;

    /**
     * 인증을 위한 인증 번호 이메일 전송
     * @param messageDto 구독한 메시지를 담고 있는 MessageDto 객체
     */
    @RabbitListener(queues = "${spring.rabbitmq.bindings[0].queue.name}")
    public void receiveMessageForVerify(AuthMessageDto messageDto) {
        try {
            processForVerify(messageDto);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
        }
    }

    /**
     * 새로운 비밀번호 전송을 위한 이메일 전송
     * @param messageDto
     */
    @RabbitListener(queues = "${spring.rabbitmq.bindings[1].queue.name}")
    public void receiveMessageForResetPassword(AuthMessageDto messageDto) {

        try{
            emailService.sendMailWithKey(messageDto.getEmail(), messageDto.getType(), messageDto.getKey()); // 새로운 비밀번호만 전송하면 됨, 비밀번호 수정은 service 단에서
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
        }
    }

    /**
     * alarm schedule 이메일 전송
     * @param messageDto
     */
    @RabbitListener(queues = "${spring.rabbitmq.bindings[2].queue.name}")
    public void receiveMessageForSchedule(AlarmMessageDto messageDto) {

        try{
            scheduleMailService.sendScheduleAlert(messageDto); // 수정
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
        }
    }

    void processForVerify(AuthMessageDto messageDto) {

        emailService.sendMailWithKey(messageDto.getEmail(), messageDto.getType(), messageDto.getKey());
        mailVerificationCacheRepository.save(MailVerificationCache.builder()
                .email(messageDto.getEmail())
                .verificationKey(messageDto.getKey())
                .isVerify(false)
                .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                .build());
    }


}
