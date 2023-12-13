package com.acert.deliverycontrol.infra.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> template;

    public synchronized Object getValue(final String key) {

        this.template.setHashValueSerializer(new StringRedisSerializer());
        this.template.setValueSerializer(new StringRedisSerializer());
        return this.template.opsForValue().get(key);
    }

    public void setValue(final String key, final Object value) {
        this.setValue(key, value, false);
    }

    public void setValue(final String key, final Object value, final boolean marshal) {
        if (marshal) {
            this.template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
            this.template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        } else {
            this.template.setHashValueSerializer(new StringRedisSerializer());
            this.template.setValueSerializer(new StringRedisSerializer());
        }
        this.template.opsForValue().set(key, value);
    }

    public void resetValue(final String key) {
        this.reset(key, false);
    }

    private void reset(final String key, final boolean marshal) {
        if (marshal) {
            this.template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
            this.template.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        } else {
            this.template.setHashValueSerializer(new StringRedisSerializer());
            this.template.setValueSerializer(new StringRedisSerializer());
        }
        this.template.delete(key);
    }
}

