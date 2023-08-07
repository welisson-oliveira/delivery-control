package com.acert.deliverycontrol.infra.repository;

import com.acert.deliverycontrol.domain.order.Order;
import com.acert.deliverycontrol.domain.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.client.id = :clientId AND (o.status = 'CREATED' OR o.status = 'IN_PROGRESS')")
    List<Order> getAllByClientIdWithStatusCreatedOrInProgress(Long clientId);

    @Query("SELECT o FROM Order o WHERE o.client.id = :clientId AND o.status = 'DONE'")
    List<Order> getAllByClientIdWithStatusDone(Long clientId);

    @Query("SELECT o FROM Order o WHERE o.client.id = :clientId AND o.status = 'CANCELED'")
    List<Order> getAllByClientIdWithStatusCanceled(Long clientId);

    @Query("SELECT o FROM Order o WHERE o.status = 'CREATED' OR o.status = 'IN_PROGRESS'")
    List<Order> getActiveOrders();

    List<Order> getOrderByStatus(OrderStatus orderStatus);

}

