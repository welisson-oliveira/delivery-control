package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.application.OrderService;
import com.acert.deliverycontrol.domain.order.Order;
import com.acert.deliverycontrol.infra.dto.order.CreateOrder;
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
    public List<Order> getAllOrders() {
        return this.orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public OrderDTO getOrderById(@PathVariable final Long id) {
        return this.mapper.toDTO(this.orderService.getOrderById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO createOrder(@RequestBody final CreateOrder order) {
        return this.mapper.toDTO(this.orderService.createOrder(order.getDescription()));
    }

    @PutMapping("/{id}")
    public OrderDTO updateOrder(@PathVariable final Long id, @RequestBody final OrderDTO order) {
        return this.mapper.toDTO(this.orderService.updateOrder(id, this.mapper.toEntity(order)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable final Long id) {
        this.orderService.deleteOrder(id);
    }

}

