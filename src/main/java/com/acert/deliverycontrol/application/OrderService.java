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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;

    public List<Order> getAllOrders() {
        return this.orderRepository.findAll();
    }

    public Order getOrderById(final Long id) {
        return this.orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Order not found with ID: " + id));
    }

    @Transactional
    public Order createOrder(final String description) {

        final Client loggedClient = new Client(1L, "welisson", "welisson@email.com", "13123123123", "Address");

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
                .orElseThrow(() -> new DataNotFoundException("Order not found with ID: " + id));
    }

    @Transactional
    public void deleteOrder(final Long id) {
        if (this.orderRepository.existsById(id)) {
            this.publisher.publishEvent(new DeleteOrderEvent(id));
        } else {
            throw new DataNotFoundException("Order not found with ID: " + id);
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

    public List<Order> getActivatedOrdersByClientId(final Long clientId) {
        return this.orderRepository.getAllByClientIdWithStatusCreatedOrInProgress(clientId);
    }

    public List<Order> getDoneOrdersByClientId(final Long clientId) {
        return this.orderRepository.getAllByClientIdWithStatusDone(clientId);
    }

    public List<Order> getCanceledOrdersByClientId(final Long clientId) {
        return this.orderRepository.getAllByClientIdWithStatusCanceled(clientId);
    }

    public List<Order> getAllActivatedOrders() {
        return this.orderRepository.getActiveOrders();
    }

    public List<Order> getAllFinishedOrders() {
        return this.orderRepository.getOrderByStatus(OrderStatus.DONE);
    }

    public List<Order> getAllCanceledOrders() {
        return this.orderRepository.getOrderByStatus(OrderStatus.CANCELED);
    }
}

