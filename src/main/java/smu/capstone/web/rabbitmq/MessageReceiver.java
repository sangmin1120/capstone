package smu.capstone.web.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import smu.capstone.object.member.service.EmailService;
import smu.capstone.web.jwt.redisdomain.MailVerificationCache;
import smu.capstone.web.jwt.redisrepository.MailVerificationCacheRepository;

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
    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void receiveMessageForVerify(MessageDto messageDto) {
        try {
            processForVerify(messageDto); // 인증 메일 같은거만 처리할 수 있음, 새로운 비밀번호 변경은 아직
        } catch (Exception e) {
            throw new AmqpRejectAndDontRequeueException("Failed to process message", e);
        }
    }

    void processForVerify(MessageDto messageDto) {

        String certificationKey = emailService.sendCertificationKey(messageDto.getEmail(), messageDto.getType());
        mailVerificationCacheRepository.save(MailVerificationCache.builder()
                .email(messageDto.getEmail())
                .verificationKey(certificationKey)
                .isVerify(false)
                .expiration(CERTIFICATION_KEY_EXPIRE_SECONDS)
                .build());
    }
}
