package com.acert.deliverycontrol.domain.client;

import com.acert.deliverycontrol.domain.delivery.Delivery;
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
@Table(name = "client")
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
@AllArgsConstructor
public class Client {

    @Id
    @SequenceGenerator(name = "client_seq", sequenceName = "client_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_seq")
    private Long id;
    private String name;
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;
    private String address;

    @OneToMany(mappedBy = "client")
    private final List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    private final List<Delivery> deliveries = new ArrayList<>();

    public void updateClient(final Client updatedClient) {
        this.name = updatedClient.getName();
        this.email = updatedClient.getEmail();
        this.phoneNumber = updatedClient.getPhoneNumber();
        this.address = updatedClient.getAddress();

    }

}

