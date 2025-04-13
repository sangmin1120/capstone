package smu.capstone.intrastructure.rabbitmq.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import smu.capstone.intrastructure.rabbitmq.dto.MessageDto;
import smu.capstone.intrastructure.mail.service.EmailService;
import smu.capstone.intrastructure.redis.domain.MailVerificationCache;
import smu.capstone.intrastructure.redis.repository.MailVerificationCacheRepository;

@Component
@RequiredArgsConstructor
public class MessageReceiver {

    private static final Long CERTIFICATION_KEY_EXPIRE_SECONDS = 10 * 60L;

    private final EmailService emailService;
    private final MailVerificationCacheRepository mailVerificationCacheRepository;

    /**
     * Queue에서 메시지를 구독
     * ListenerExecutionFailedException 메일은 보내지는 데, 예외가 발생함??????????????
     * @param messageDto 구독한 메시지를 담고 있는 MessageDto 객체
     */
    @RabbitListener(queues = "${spring.rabbitmq.bindings[0].queue.name}")
    public void receiveMessageForVerify(MessageDto messageDto) {
        try {
            processForVerify(messageDto); // 인증 메일 같은거만 처리할 수 있음, 새로운 비밀번호 변경은 아직
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.bindings[1].queue.name}") // 새로운 비밀번호 메일 전송
    public void receiveMessageForResetPassword(MessageDto messageDto) {

        try{
            processForVerify(messageDto);
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
        }
    }

    void processForVerify(MessageDto messageDto) {

        emailService.sendMailWithKey(messageDto.getEmail(), messageDto.getType(), messageDto.getPayLoad());
        mailVerificationCacheRepository.save(MailVerificationCache.builder()
                .email(messageDto.getEmail())
                .verificationKey(messageDto.getPayLoad())
                .isVerify(false)
                .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                .build());
    }
}
