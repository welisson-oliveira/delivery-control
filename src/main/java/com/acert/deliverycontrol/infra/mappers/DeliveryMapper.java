package com.acert.deliverycontrol.infra.mappers;

import com.acert.deliverycontrol.domain.delivery.Delivery;
import com.acert.deliverycontrol.infra.dto.delivery.DeliveryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeliveryMapper {

    private final OrderMapper orderMapper;

    public DeliveryDTO toDTO(final Delivery delivery) {
        return new DeliveryDTO(delivery.getId(), delivery.getAddress(), delivery.getStatus(),
                delivery.getClient().getId(), this.orderMapper.toDTOs(delivery.getOrders()));
    }

    public List<DeliveryDTO> toDTOs(final List<Delivery> allDeliveries) {
        return allDeliveries.stream().map(this::toDTO).toList();
    }
}

