package com.acert.deliverycontrol.infra.config.redis;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.stereotype.Component;

@Component
@SecurityScheme(name = OpenApiConfig.SECURITY_CONFIG_NAME, in = SecuritySchemeIn.HEADER, type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    public static final String SECURITY_CONFIG_NAME = "App Bearer token";
}
