package com.acert.deliverycontrol.config.mockauth;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = ClientSecurityContext.class)
public @interface WithUser {

    long id() default 1L;

    String username() default "ADMIN@EMAIL>COM";

    String name() default "Admin";

    String phoneNumber() default "12 123451234";

    String address() default "UM ENDEREÇO QUALQUER";

    String[] authorities() default {"ADMIN", "CLIENT"};
}
