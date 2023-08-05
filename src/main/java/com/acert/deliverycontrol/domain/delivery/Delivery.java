package com.acert.deliverycontrol.domain.delivery;

import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.domain.order.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "delivery")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Delivery {

    @Id
    @SequenceGenerator(name = "delivery_seq", sequenceName = "delivery_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_seq")
    private Long id;
    private String address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Order> orders = new ArrayList<>();

    public void updateDelivery(final Delivery updatedDelivery) {
        this.address = updatedDelivery.getAddress();
        this.status = updatedDelivery.getStatus();
    }

    public void addOrder(final Order order) {
        this.orders.add(order);
    }
}

