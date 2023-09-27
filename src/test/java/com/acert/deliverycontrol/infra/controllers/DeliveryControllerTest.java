package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.config.AbstractTestsConfig;
import com.acert.deliverycontrol.config.ClearContext;
import com.acert.deliverycontrol.config.mockauth.WithUser;
import com.acert.deliverycontrol.domain.delivery.Delivery;
import com.acert.deliverycontrol.domain.delivery.DeliveryStatus;
import com.acert.deliverycontrol.domain.order.InvalidStatusException;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ClearContext
@WithUser
class DeliveryControllerTest extends AbstractTestsConfig {


    @Test
    void shouldReturnAllDeliveries() throws Exception {
        this.shouldCreateOrderAndDelivery2();
        this.mockMvc.perform(get("/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/delivery/return-all.json")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnClientById() throws Exception {
        this.shouldCreateOrderAndDelivery2();

        this.mockMvc.perform(get("/deliveries/1"))
                .andExpect(content().json(this.readFileAsString("files/output/delivery/return-delivery-3.json")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFinalizeDelivery() throws Exception {
        this.shouldCreateAndFinalizeOrderAndDelivery();
        this.mockMvc.perform(patch("/deliveries/1/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/delivery/return-delivery-delivered.json")))
                .andExpect(status().isOk());
    }

    @Test
    void cantChangeDeliveryStatusFromCanceledToInProgress() {
        final Delivery delivery = new Delivery(1L, "address", DeliveryStatus.CANCELED, null);
        final InvalidStatusException exception = Assert.assertThrows(
                InvalidStatusException.class,
                delivery::start
        );

        Assertions.assertEquals("can't change from: CANCELED to: IN_PROGRESS", exception.getMessage());
    }

    @Test
    void cantChangeDeliveryStatusFromCanceledToCanceled() {
        final Delivery delivery = new Delivery(1L, "address", DeliveryStatus.CANCELED, null);
        final InvalidStatusException exception = Assert.assertThrows(
                InvalidStatusException.class,
                delivery::cancel
        );

        Assertions.assertEquals("can't change from: CANCELED to: CANCELED", exception.getMessage());
    }

    @Test
    void cantChangeDeliveryStatusFromCanceledToFinish() {
        final Delivery delivery = new Delivery(1L, "address", DeliveryStatus.CANCELED, null);
        final InvalidStatusException exception = Assert.assertThrows(
                InvalidStatusException.class,
                delivery::finish
        );

        Assertions.assertEquals("can't change from: CANCELED to: DELIVERED", exception.getMessage());
    }

    private void shouldCreateAndFinalizeOrderAndDelivery() throws Exception {
        final String create = this.readFileAsString("files/input/order/insert-order.json");

        this.mockMvc.perform(post("/orders")
                        .content(create)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order.json")))
                .andExpect(status().isCreated());

        this.mockMvc.perform(patch("/orders/1/in-progress")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(patch("/orders/1/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private void shouldCreateOrderAndDelivery2() throws Exception {
        final String create = this.readFileAsString("files/input/order/insert-order.json");

        this.mockMvc.perform(post("/orders")
                        .content(create)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order.json")))
                .andExpect(status().isCreated());

    }


}
