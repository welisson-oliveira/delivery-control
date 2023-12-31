package com.acert.deliverycontrol.infra.dto.delivery;

import com.acert.deliverycontrol.domain.delivery.DeliveryStatus;
import com.acert.deliverycontrol.infra.dto.order.OrderDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class DeliveryDTO {
    private Long id;
    private String address;
    private DeliveryStatus status;
    private Long clientId;
    private final Set<OrderDTO> orders;
}
