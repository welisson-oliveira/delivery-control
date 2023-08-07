package com.acert.deliverycontrol.infra.dto.order;

import com.acert.deliverycontrol.domain.order.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDTO {
    private Long id;
    private String description;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Long clientId;
}
