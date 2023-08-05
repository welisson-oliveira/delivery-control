package com.acert.deliverycontrol.infra.events;

import com.acert.deliverycontrol.domain.order.Order;
import org.springframework.context.ApplicationEvent;

public class CreateOrderEvent extends ApplicationEvent {
    public CreateOrderEvent(final Order order) {
        super(order);
    }

    @Override
    public Order getSource() {
        return (Order) super.getSource();
    }

}
