package com.acert.deliverycontrol.infra.events;

import org.springframework.context.ApplicationEvent;

public class FinishedOrderEvent extends ApplicationEvent {
    public FinishedOrderEvent(final Long orderId) {
        super(orderId);
    }

    @Override
    public Long getSource() {
        return (Long) super.getSource();
    }
}
