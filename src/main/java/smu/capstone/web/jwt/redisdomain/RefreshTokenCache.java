package smu.capstone.web.jwt.redisdomain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@Builder
@RedisHash("refresh_tokens")
public class RefreshTokenCache {
    @Id
    String refreshToken;

    @TimeToLive
    Long expiration;
}
