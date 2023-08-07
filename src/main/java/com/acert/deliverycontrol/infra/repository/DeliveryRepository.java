package com.acert.deliverycontrol.infra.repository;

import com.acert.deliverycontrol.domain.delivery.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    @Query("SELECT d FROM Delivery d JOIN d.orders o WHERE o.id = :orderId")
    Optional<Delivery> findDeliveryByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT d FROM Delivery d WHERE d.client.id = :clientId AND (d.status = 'WAITING' OR d.status = 'IN_PROGRESS')")
    Optional<Delivery> findActiveByClientId(@Param("clientId") Long clientId);

}

