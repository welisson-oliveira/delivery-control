package com.acert.deliverycontrol.application;

import com.acert.deliverycontrol.application.exceptions.DataNotFoundException;
import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.domain.delivery.Delivery;
import com.acert.deliverycontrol.domain.delivery.DeliveryStatus;
import com.acert.deliverycontrol.domain.order.Order;
import com.acert.deliverycontrol.infra.events.CanceledOrderEvent;
import com.acert.deliverycontrol.infra.events.CreateOrderEvent;
import com.acert.deliverycontrol.infra.events.DeleteOrderEvent;
import com.acert.deliverycontrol.infra.events.FinishedOrderEvent;
import com.acert.deliverycontrol.infra.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public List<Delivery> getAllDeliveries() {
        return this.deliveryRepository.findAll();
    }

    public Delivery getDeliveryById(final Long id) {
        return this.deliveryRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Delivery not found with ID: " + id));
    }


    private Delivery createDelivery(final Delivery delivery) {
        return this.deliveryRepository.save(delivery);
    }

    @EventListener
    @Transactional
    public void createOrder(final CreateOrderEvent createOrderEvent) {

        final Client loggedClient = (Client) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        final Optional<Delivery> activeByClientId = this.deliveryRepository.findActiveByClientId(loggedClient.getId());

        final Delivery delivery = activeByClientId.orElseGet(() -> new Delivery(null, loggedClient.getAddress(), DeliveryStatus.WAITING, loggedClient));
        final Order order = createOrderEvent.getSource();
        delivery.addOrder(order);
        this.createDelivery(delivery);
        DeliveryService.log.info("Entrega '" + delivery.getId() + "' foi criada para o pedido: '" + order.getDescription() + "' do cliente: '" + delivery.getClientName() + "'");
    }

    @EventListener
    public void startDelivery(final FinishedOrderEvent finishedOrderEvent) {

        final Long orderId = finishedOrderEvent.getSource();
        this.deliveryRepository.findDeliveryByOrderId(orderId).ifPresent(delivery -> {
            delivery.start();
            this.deliveryRepository.save(delivery);
        });
    }

    @EventListener
    public void cancel(final CanceledOrderEvent canceledOrderEvent) {
        final Long orderId = canceledOrderEvent.getSource();
        this.deliveryRepository.findDeliveryByOrderId(orderId).ifPresent(delivery -> {
            delivery.cancel();
            delivery.start();
            this.deliveryRepository.save(delivery);
        });
    }

    @EventListener
    public void delete(final DeleteOrderEvent canceledOrderEvent) {
        final Long orderId = canceledOrderEvent.getSource();
        this.deliveryRepository.findDeliveryByOrderId(orderId).ifPresent(delivery -> {
            delivery.removeOrderById(orderId);
            if (delivery.canDelete()) {
                this.deliveryRepository.delete(delivery);
                DeliveryService.log.info("Removeu o pedido com id: '" + orderId + "' da entrega: '" + delivery.getId() + "' para o cliente '" + delivery.getClientName() + "'");
            } else {
                this.deliveryRepository.save(delivery);
                DeliveryService.log.info("Excluiu a entrega: '" + delivery.getId() + "'");
            }
        });
    }

    public Delivery finalizeDelivery(final Long id) {
        final Delivery deliveryById = this.getDeliveryById(id);
        deliveryById.finish();
        return this.deliveryRepository.save(deliveryById);
    }
}

