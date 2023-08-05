package com.acert.deliverycontrol.application;

import com.acert.deliverycontrol.application.exceptions.DataNotFoundException;
import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.domain.delivery.Delivery;
import com.acert.deliverycontrol.domain.delivery.DeliveryStatus;
import com.acert.deliverycontrol.infra.events.CreateOrderEvent;
import com.acert.deliverycontrol.infra.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public List<Delivery> getAllDeliveries() {
        return this.deliveryRepository.findAll();
    }

    public Delivery getDeliveryById(final Long id) {
        return this.deliveryRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Delivery not found with ID: " + id));
    }

    @Transactional
    public Delivery createDelivery(final Delivery delivery) {
        return this.deliveryRepository.save(delivery);
    }

    @Transactional
    public Delivery updateDelivery(final Long id, final Delivery updatedDelivery) {
        return this.deliveryRepository.findById(id)
                .map(delivery -> {
                    delivery.updateDelivery(updatedDelivery);
                    return this.deliveryRepository.save(delivery);
                })
                .orElseThrow(() -> new DataNotFoundException("Delivery not found with ID: " + id));
    }

    @Transactional
    public void deleteDelivery(final Long id) {
        if (this.deliveryRepository.existsById(id)) {
            this.deliveryRepository.deleteById(id);
        } else {
            throw new DataNotFoundException("Delivery not found with ID: " + id);
        }
    }

    @EventListener
    public Delivery createOrder(final CreateOrderEvent createOrderEvent) {

        final Client loggedClient = new Client(null, "welisson", "welisson@email.com", "13123123123", "Address");

        final Delivery delivery = new Delivery(null, loggedClient.getAddress(), DeliveryStatus.WAITING, loggedClient);
        delivery.addOrder(createOrderEvent.getSource());
        return this.createDelivery(delivery);
    }
}

