package com.acert.deliverycontrol.application;

import com.acert.deliverycontrol.application.exceptions.DataNotFoundException;
import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.domain.order.Order;
import com.acert.deliverycontrol.domain.order.OrderStatus;
import com.acert.deliverycontrol.infra.events.CanceledOrderEvent;
import com.acert.deliverycontrol.infra.events.CreateOrderEvent;
import com.acert.deliverycontrol.infra.events.DeleteOrderEvent;
import com.acert.deliverycontrol.infra.events.FinishedOrderEvent;
import com.acert.deliverycontrol.infra.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderService {

    public static final String ORDER_NOT_FOUND_WITH_ID = "Order not found with ID:";
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;

    public Set<Order> getAllOrders() {
        return new HashSet<>(this.orderRepository.findAll());
    }

    public Order getOrderById(final Long id) {
        return this.orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException(OrderService.ORDER_NOT_FOUND_WITH_ID + " " + id));
    }

    @Transactional
    public Order createOrder(final String description) {
        final Client loggedClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final Order order = new Order(description, loggedClient);

        final Order savedOrder = this.orderRepository.save(order);
        this.publisher.publishEvent(new CreateOrderEvent(order));

        return savedOrder;
    }

    @Transactional
    public Order updateOrder(final Long id, final String description) {
        return this.orderRepository.findById(id)
                .map(order -> {
                    order.updateOrder(description);
                    return this.orderRepository.save(order);
                })
                .orElseThrow(() -> new DataNotFoundException(OrderService.ORDER_NOT_FOUND_WITH_ID + " " + id));
    }

    @Transactional
    public void deleteOrder(final Long id) {
        if (this.orderRepository.existsById(id)) {
            this.publisher.publishEvent(new DeleteOrderEvent(id));
        } else {
            throw new DataNotFoundException(OrderService.ORDER_NOT_FOUND_WITH_ID + " " + id);
        }
    }

    @Transactional
    public Order changeStatus(final Long id, final OrderStatus orderStatus) {
        final Order order = this.getOrderById(id);
        order.nextStatus(orderStatus);
        final Order orderToSave = this.orderRepository.save(order);
        if (OrderStatus.DONE.equals(orderStatus)) {
            this.publisher.publishEvent(new FinishedOrderEvent(order.getId()));
        } else if (OrderStatus.CANCELED.equals(orderStatus)) {
            this.publisher.publishEvent(new CanceledOrderEvent(order.getId()));
        }
        return orderToSave;
    }

    public Set<Order> getActivatedOrdersByClientId(final Long clientId) {
        return this.orderRepository.getAllByClientIdWithStatusCreatedOrInProgress(clientId);
    }

    public Set<Order> getDoneOrdersByClientId(final Long clientId) {
        return this.orderRepository.getAllByClientIdWithStatusDone(clientId);
    }

    public Set<Order> getCanceledOrdersByClientId(final Long clientId) {
        return this.orderRepository.getAllByClientIdWithStatusCanceled(clientId);
    }

    public Set<Order> getAllActivatedOrders() {
        return this.orderRepository.getActiveOrders();
    }

    public Set<Order> getAllFinishedOrders() {
        return this.orderRepository.getOrderByStatus(OrderStatus.DONE);
    }

    public Set<Order> getAllCanceledOrders() {
        return this.orderRepository.getOrderByStatus(OrderStatus.CANCELED);
    }
}

