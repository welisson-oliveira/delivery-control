package com.acert.deliverycontrol.infra.repository;

import com.acert.deliverycontrol.domain.delivery.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}

