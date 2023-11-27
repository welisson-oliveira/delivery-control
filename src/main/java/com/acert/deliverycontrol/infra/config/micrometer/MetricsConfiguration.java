package com.acert.deliverycontrol.infra.config.micrometer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MetricsConfiguration {

    private Counter authUserSuccess;
    private Counter authUserErrors;

    public MetricsConfiguration(MeterRegistry registry){
        authUserSuccess = Counter.builder("auth_user_success")
                .description("Usuarios autenticados")
                .register(registry);

        authUserErrors = Counter.builder("auth_user_error")
                .description("Erros de login")
                .register(registry);
    }

    public void authUserSuccessIncrement(){
        this.authUserSuccess.increment();
    }

    public void authUserErrorsIncrement(){
        this.authUserErrors.increment();
    }
}
