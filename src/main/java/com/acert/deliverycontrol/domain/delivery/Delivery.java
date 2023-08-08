package com.acert.deliverycontrol.domain.delivery;

import com.acert.deliverycontrol.domain.client.Client;
import com.acert.deliverycontrol.domain.order.InvalidStatusException;
import com.acert.deliverycontrol.domain.order.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @JoinTable(
            name = "delivery_orders",
            joinColumns = @JoinColumn(name = "delivery_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private final List<Order> orders = new ArrayList<>();

    public void addOrder(final Order order) {
        this.orders.add(order);
    }

    public void start() {
        if (this.status.nextStatus().stream().anyMatch(s -> s.equals(DeliveryStatus.IN_PROGRESS)) && this.canStart()) {
            this.status = DeliveryStatus.IN_PROGRESS;
        } else {
            throw new InvalidStatusException("can't change from: " + this.status + " to: " + DeliveryStatus.IN_PROGRESS);
        }
    }

    public void cancel() {
        if (this.status.nextStatus().stream().anyMatch(s -> s.equals(DeliveryStatus.CANCELED)) && this.canCancel()) {
            this.status = DeliveryStatus.CANCELED;
        } else {
            throw new InvalidStatusException("can't change from: " + this.status + " to: " + DeliveryStatus.CANCELED);
        }
    }

    public void finish() {
        if (this.status.nextStatus().stream().anyMatch(s -> s.equals(DeliveryStatus.DELIVERED)) && this.canFinalize()) {
            this.status = DeliveryStatus.DELIVERED;
        } else {
            throw new InvalidStatusException("can't change from: " + this.status + " to: " + DeliveryStatus.DELIVERED);
        }
    }

    private boolean allDone() {
        return this.orders.stream().allMatch(Order::isFinished);
    }

    private boolean canStart() {
        return this.allDone();
    }

    private boolean canCancel() {
        return this.orders.stream().allMatch(Order::isCanceled);
    }

    private boolean canFinalize() {
        return this.allDone();
    }

    public void removeOrderById(final Long orderId) {
        final Optional<Order> optionalOrder = this.orders.stream().filter(order -> order.getId().equals(orderId)).findFirst();
        optionalOrder.ifPresent(this.orders::remove);
    }

    public boolean canDelete() {
        return this.orders.isEmpty();
    }
}

