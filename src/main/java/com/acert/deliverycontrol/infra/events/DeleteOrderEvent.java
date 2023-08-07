package com.acert.deliverycontrol.infra.events;

import org.springframework.context.ApplicationEvent;

public class DeleteOrderEvent extends ApplicationEvent {
    public DeleteOrderEvent(final Long orderId) {
        super(orderId);
    }

    @Override
    public Long getSource() {
        return (Long) super.getSource();
    }
}
