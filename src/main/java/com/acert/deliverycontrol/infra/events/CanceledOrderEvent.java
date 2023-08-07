package com.acert.deliverycontrol.infra.events;

import org.springframework.context.ApplicationEvent;

public class CanceledOrderEvent extends ApplicationEvent {
    public CanceledOrderEvent(final Long orderId) {
        super(orderId);
    }

    @Override
    public Long getSource() {
        return (Long) super.getSource();
    }
}
