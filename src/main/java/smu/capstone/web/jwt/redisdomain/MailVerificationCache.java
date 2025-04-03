package smu.capstone.web.jwt.redisdomain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@AllArgsConstructor
@Builder
@RedisHash("mail_verification")
public class MailVerificationCache {
    @Id
    String email;
    String verificationKey;
    Boolean isVerify;
    @TimeToLive
    Long expiration;
}
