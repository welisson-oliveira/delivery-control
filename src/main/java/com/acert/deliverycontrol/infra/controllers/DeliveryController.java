package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.application.DeliveryService;
import com.acert.deliverycontrol.infra.dto.DeliveryDTO;
import com.acert.deliverycontrol.infra.mappers.DeliveryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryMapper mapper;

    @GetMapping
    public List<DeliveryDTO> getAllDeliveries() {
        return this.mapper.toDTOs(this.deliveryService.getAllDeliveries());
    }

    @GetMapping("/{id}")
    public DeliveryDTO getDeliveryById(@PathVariable final Long id) {
        return this.mapper.toDTO(this.deliveryService.getDeliveryById(id));
    }

}

