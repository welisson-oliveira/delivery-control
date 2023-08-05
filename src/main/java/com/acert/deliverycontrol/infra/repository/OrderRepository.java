package com.acert.deliverycontrol.infra.repository;

import com.acert.deliverycontrol.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

