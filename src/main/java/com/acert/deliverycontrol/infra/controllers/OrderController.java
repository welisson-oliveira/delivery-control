package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.application.OrderService;
import com.acert.deliverycontrol.domain.order.OrderStatus;
import com.acert.deliverycontrol.infra.dto.order.CreateUpdateOrder;
import com.acert.deliverycontrol.infra.dto.order.OrderDTO;
import com.acert.deliverycontrol.infra.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper mapper;

    @GetMapping
    public List<OrderDTO> getAllOrders() {
        return this.mapper.toDTOs(this.orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable final Long id) {
        return this.mapper.toDTO(this.orderService.getOrderById(id));
    }

    @GetMapping("/activated")
    public List<OrderDTO> getAllActivatedOrder() {
        return this.mapper.toDTOs(this.orderService.getAllActivatedOrders());
    }

    @GetMapping("/finished")
    public List<OrderDTO> getAllFinishedOrder() {
        return this.mapper.toDTOs(this.orderService.getAllFinishedOrders());
    }

    @GetMapping("/canceled")
    public List<OrderDTO> getAllCanceledOrder() {
        return this.mapper.toDTOs(this.orderService.getAllCanceledOrders());
    }

    @GetMapping("/clients/{clientId}/activated")
    public List<OrderDTO> getActivatedOrdersByClientId(@PathVariable final Long clientId) {
        return this.mapper.toDTOs(this.orderService.getActivatedOrdersByClientId(clientId));
    }

    @GetMapping("/clients/{clientId}/finished")
    public List<OrderDTO> getDoneOrdersByClientId(@PathVariable final Long clientId) {
        return this.mapper.toDTOs(this.orderService.getDoneOrdersByClientId(clientId));
    }

    @GetMapping("/clients/{clientId}/canceled")
    public List<OrderDTO> getCanceledOrdersByClientId(@PathVariable final Long clientId) {
        return this.mapper.toDTOs(this.orderService.getCanceledOrdersByClientId(clientId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody final CreateUpdateOrder order) {
        return this.mapper.toDTO(this.orderService.createOrder(order.getDescription()));
    }

    @PutMapping("/{id}")
    public OrderDTO updateOrder(@PathVariable final Long id, @RequestBody final CreateUpdateOrder order) {
        return this.mapper.toDTO(this.orderService.updateOrder(id, order.getDescription()));
    }

    @PatchMapping("/{id}/in-progress")
    public OrderDTO start(@PathVariable final Long id) {
        return this.mapper.toDTO(this.orderService.changeStatus(id, OrderStatus.IN_PROGRESS));
    }

    @PatchMapping("/{id}/canceled")
    public OrderDTO cancel(@PathVariable final Long id) {
        return this.mapper.toDTO(this.orderService.changeStatus(id, OrderStatus.CANCELED));
    }

    @PatchMapping("/{id}/finished")
    public OrderDTO finished(@PathVariable final Long id) {
        return this.mapper.toDTO(this.orderService.changeStatus(id, OrderStatus.DONE));
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable final Long id) {
        this.orderService.deleteOrder(id);
    }

}

