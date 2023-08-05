package com.acert.deliverycontrol.infra.mappers;

import com.acert.deliverycontrol.domain.order.Order;
import com.acert.deliverycontrol.infra.dto.order.OrderDTO;
import com.acert.deliverycontrol.infra.repository.ClientRepository;
import com.acert.deliverycontrol.infra.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;

    public OrderDTO toDTO(final Order order) {
        return new OrderDTO(order.getId(), order.getDescription(), order.getStatus(),
                order.getClient().getId(), order.getOrderDate());
    }

    public Order toEntity(final OrderDTO dto) {
        if (Objects.isNull(dto.getId())) {
            return new Order(null, dto.getDescription(), dto.getStatus(), this.clientRepository.getReferenceById(dto.getClientId()), dto.getOrderDate());
        }
        return this.orderRepository.getReferenceById(dto.getId());
    }
}

