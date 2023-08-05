package com.acert.deliverycontrol.infra.repository;

import com.acert.deliverycontrol.domain.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {
}

