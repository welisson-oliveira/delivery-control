package com.acert.deliverycontrol.infra.dto;

import com.acert.deliverycontrol.domain.delivery.DeliveryStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryDTO {
    private Long id;
    private String address;
    private DeliveryStatus status;
    private Long clientId;
}
