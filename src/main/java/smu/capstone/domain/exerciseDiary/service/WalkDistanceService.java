package smu.capstone.domain.exerciseDiary.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalkDistanceService {

    private final StringRedisTemplate redisTemplate;

    public void addDistance(Long userId, double delta) {
        String key = "walk:distance:" + userId;
        redisTemplate.opsForValue().increment(key, delta);
    }

    public double getTotalDistance(Long userId) {
        String key = "walk:distance:" + userId;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Double.parseDouble(value) : 0.0;
    }

    public void clearDistance(Long userId) {
        String key = "walk:distance:" + userId;
        redisTemplate.delete(key);
    }
}

