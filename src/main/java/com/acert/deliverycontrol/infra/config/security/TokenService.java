package com.acert.deliverycontrol.infra.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenService {

    protected final RedisService redisService;
    private static final String TOKEN = "token";

    public String getCachedToken() {
        return (String) this.redisService.getValue(TokenService.TOKEN);
    }

    public void setToken(final String token) {
        this.redisService.setValue(TokenService.TOKEN, token);
    }

    public void reset() {
        this.redisService.resetValue(TokenService.TOKEN);
    }
}

