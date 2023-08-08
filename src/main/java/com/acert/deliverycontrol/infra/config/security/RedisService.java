package com.acert.deliverycontrol.infra.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Service
@Log4j2
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> template;

    public synchronized Object getValue(final String key) {

        this.template.setHashValueSerializer(new StringRedisSerializer());
        this.template.setValueSerializer(new StringRedisSerializer());
        return this.template.opsForValue().get(key);
    }

    public void setValue(final String key, final Object value) {
        this.setValue(key, value, TimeUnit.HOURS, 5, false);
    }

    public void setValue(final String key, final Object value, final TimeUnit unit, final long timeout, final boolean marshal) {
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

