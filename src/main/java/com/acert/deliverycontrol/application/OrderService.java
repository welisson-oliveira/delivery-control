package com.acert.deliverycontrol.application;

import com.acert.deliverycontrol.application.exceptions.DataNotFoundException;
import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.domain.order.Order;
import com.acert.deliverycontrol.infra.events.CreateOrderEvent;
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

        final Client loggedClient = new Client(null, "welisson", "welisson@email.com", "13123123123", "Address");

        final Order order = new Order(description, loggedClient);

        final Order savedOrder = this.orderRepository.save(order);
        this.publisher.publishEvent(new CreateOrderEvent(savedOrder));
        return savedOrder;
    }


    @Transactional
    public Order updateOrder(final Long id, final Order updatedOrder) {
        return this.orderRepository.findById(id)
                .map(order -> {
                    order.updateOrder(updatedOrder);
                    return this.orderRepository.save(order);
                })
                .orElseThrow(() -> new DataNotFoundException("Order not found with ID: " + id));
    }

    @Transactional
    public void deleteOrder(final Long id) {
        if (this.orderRepository.existsById(id)) {
            this.orderRepository.deleteById(id);
        } else {
            throw new DataNotFoundException("Order not found with ID: " + id);
        }
    }
}

