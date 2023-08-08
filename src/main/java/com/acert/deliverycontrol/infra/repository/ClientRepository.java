package com.acert.deliverycontrol.infra.repository;

import com.acert.deliverycontrol.domain.client.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<UserDetails> findByEmail(String email);
}

