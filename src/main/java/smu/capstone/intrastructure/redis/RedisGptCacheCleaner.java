package smu.capstone.intrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisGptCacheCleaner {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 매일 새벽 1시에 GPT 관련 캐시 삭제
     */
    @Scheduled(cron = "0 0 1 * * *") // 매일 01:00
    public void clearGptCache() {
        // 예: GPT 관련 키는 "gpt:" prefix 를 가지고 있어야 함
        Set<String> keys = stringRedisTemplate.keys("gpt:*");
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            System.out.println("GPT Redis 캐시 삭제 완료: " + keys.size() + "개 삭제됨");
        }
    }
}

