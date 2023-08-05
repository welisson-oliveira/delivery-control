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
@Table(name = "order")
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

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    public Order(final String description, final Client client) {
        this.description = description;
        this.status = OrderStatus.CREATED;
        this.client = client;
        this.orderDate = LocalDateTime.now();
    }

    public void updateOrder(final Order updatedOrder) {
        this.description = updatedOrder.getDescription();
        this.status = updatedOrder.getStatus();
    }
}

