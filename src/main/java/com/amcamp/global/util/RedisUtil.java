package com.amcamp.global.util;

import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisUtil {
    private final StringRedisTemplate redisTemplate;

    public <T> void setDataExpire(final String key, String value, final long durationHours) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        final Duration expireDuration = Duration.ofHours(durationHours);

        valueOperations.set(key, value, expireDuration);
    }

    public <T> Optional<String> getData(final String key) {
        final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        final String value = valueOperations.get(key);

        return Optional.ofNullable(value);
    }

    public <T> void deleteData(final String key) {
        redisTemplate.unlink(key);
    }
}
