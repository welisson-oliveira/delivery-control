package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.application.DeliveryService;
import com.acert.deliverycontrol.infra.dto.DeliveryDTO;
import com.acert.deliverycontrol.infra.mappers.DeliveryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryDTO createDelivery(@RequestBody final DeliveryDTO delivery) {
        return this.mapper.toDTO(this.deliveryService.createDelivery(this.mapper.toEntity(delivery)));
    }

    @PutMapping("/{id}")
    public DeliveryDTO updateDelivery(@PathVariable final Long id, @RequestBody final DeliveryDTO delivery) {
        return this.mapper.toDTO(this.deliveryService.updateDelivery(id, this.mapper.toEntity(delivery)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDelivery(@PathVariable final Long id) {
        this.deliveryService.deleteDelivery(id);
    }
}

