package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.config.AbstractTestsConfig;
import com.acert.deliverycontrol.config.ClearContext;
import com.acert.deliverycontrol.config.mockauth.WithUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ClearContext
@WithUser
public class DeliveryControllerTest extends AbstractTestsConfig {


    @Test
    public void shouldReturnAllDeliveries() throws Exception {
        this.shouldCreateOrderAndDelivery2();
        this.mockMvc.perform(get("/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/delivery/return-all.json")))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnClientById() throws Exception {
        this.shouldCreateOrderAndDelivery2();

        this.mockMvc.perform(get("/deliveries/1"))
                .andExpect(content().json(this.readFileAsString("files/output/delivery/return-delivery-3.json")))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldFinalizeDelivery() throws Exception {
        this.shouldCreateOrderAndDelivery();
        this.mockMvc.perform(patch("/deliveries/1/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/delivery/return-delivery-delivered.json")))
                .andExpect(status().isOk());
    }

    private void shouldCreateOrderAndDelivery() throws Exception {
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
