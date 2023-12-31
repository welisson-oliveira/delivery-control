package com.acert.deliverycontrol.infra.mappers;

import com.acert.deliverycontrol.domain.order.Order;
import com.acert.deliverycontrol.infra.dto.order.OrderDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    public OrderDTO toDTO(final Order order) {
        return new OrderDTO(order.getId(), order.getDescription(), order.getStatus(),
                order.getOrderDate(), order.getClient().getId());
    }

    public Set<OrderDTO> toDTOs(final Set<Order> allOrders) {
        return allOrders.stream().map(this::toDTO).collect(Collectors.toSet());
    }
}

