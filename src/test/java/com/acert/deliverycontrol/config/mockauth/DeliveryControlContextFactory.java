package com.acert.deliverycontrol.config.mockauth;

import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Annotation;

public abstract class DeliveryControlContextFactory<T extends Annotation> implements WithSecurityContextFactory<T> {
}
