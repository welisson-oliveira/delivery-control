package com.acert.deliverycontrol.domain.order;

import com.acert.deliverycontrol.domain.client.Client;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "customer_order")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Order {

    @Id
    @SequenceGenerator(name = "order_seq", sequenceName = "order_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    private Long id;
    private String description;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public Order(final String description, final Client client) {
        this.description = description;
        this.status = OrderStatus.CREATED;
        this.orderDate = LocalDateTime.now();
        this.client = client;
    }

    public void updateOrder(final String description) {
        this.description = description;
    }

    public void nextStatus(final OrderStatus orderStatus) {
        if (this.status.nextStatus().stream().anyMatch(s -> s.equals(orderStatus))) {
            this.status = orderStatus;
        } else {
            throw new InvalidStatusException("can't change from: " + this.status + " to: " + orderStatus);
        }

    }

    public boolean isFinished() {
        return OrderStatus.DONE.equals(this.status);
    }

    public boolean isCanceled() {
        return OrderStatus.CANCELED.equals(this.status);
    }

}

