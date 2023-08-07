package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.config.AbstractTestsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTests extends AbstractTestsConfig {

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-order.sql"})
    public void shouldReturnAllOrders() throws Exception {
        this.mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/order/return-all.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnAllActivatedOrders() throws Exception {
        this.mockMvc.perform(get("/orders/activated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/order/return-all-activated-orders.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnAllDoneOrders() throws Exception {
        this.mockMvc.perform(get("/orders/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-done-order.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnAllCanceledOrders() throws Exception {
        this.mockMvc.perform(get("/orders/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-canceled-order.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-order.sql"})
    public void shouldReturnOrderById() throws Exception {
        this.mockMvc.perform(get("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order.json")))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnActivatedOrdersById() throws Exception {
        this.mockMvc.perform(get("/orders/clients/1/activated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-all-activated-orders.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnDoneOrdersById() throws Exception {
        this.mockMvc.perform(get("/orders/clients/1/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-done-order.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnCanceledOrdersById() throws Exception {
        this.mockMvc.perform(get("/orders/clients/1/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-canceled-order.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql"})
    public void shouldCreateOrderAndDelivery() throws Exception {
        final String update = this.readFileAsString("files/input/order/insert-order.json");

        this.mockMvc.perform(post("/orders")
                        .content(update)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order.json")))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/delivery/return-delivery.json")))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql"})
    public void shouldCreateMoreThanOneOrderToADelivery() throws Exception {
        final String insert = this.readFileAsString("files/input/order/insert-order.json");

        this.mockMvc.perform(post("/orders")
                        .content(insert)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order.json")))
                .andExpect(status().isCreated());

        final String insert2 = this.readFileAsString("files/input/order/insert-2-order.json");

        this.mockMvc.perform(post("/orders")
                        .content(insert2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order-2.json")))
                .andExpect(status().isCreated());

        this.mockMvc.perform(get("/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/delivery/return-delivery-2.json")))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-order.sql"})
    public void shouldUpdateOrderById() throws Exception {
        final String update = this.readFileAsString("files/input/order/update-order.json");

        this.mockMvc.perform(put("/orders/1")
                        .content(update)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-updated-order.json"), true))
                .andExpect(status().isOk());

    }

    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-order.sql"})
    @Test
    public void shouldChangeStatusToInProgress() throws Exception {

        this.mockMvc.perform(patch("/orders/1/in-progress")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-order.sql"})
    @Test
    public void shouldChangeStatusToCanceled() throws Exception {

        this.mockMvc.perform(patch("/orders/1/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-order.sql"})
    @Test
    public void shouldNotChangeStatusToFinished() throws Exception {

        this.mockMvc.perform(patch("/orders/1/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Sql(scripts = {"classpath:db/client/insert-client.sql"})
    @Test
    public void shouldFinalizeTheOrderAndStartTheDelivery() throws Exception {
        this.shouldCreateOrderAndDelivery();
        this.mockMvc.perform(patch("/orders/1/in-progress")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(patch("/orders/1/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/delivery/return-delivery-in-progress.json")))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql", "classpath:db/order/insert-order.sql"})
    public void shouldRemoveOrderWithOneDelivery() throws Exception {
        this.mockMvc.perform(delete("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql"})
    public void shouldRemoveOrderAndKeepDelivery() throws Exception {

        final String insert = this.readFileAsString("files/input/order/insert-order.json");

        this.mockMvc.perform(post("/orders")
                        .content(insert)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order.json")))
                .andExpect(status().isCreated());

        final String insert2 = this.readFileAsString("files/input/order/insert-2-order.json");

        this.mockMvc.perform(post("/orders")
                        .content(insert2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order-2.json")))
                .andExpect(status().isCreated());

        this.mockMvc.perform(delete("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Sql(scripts = {"classpath:db/client/insert-client.sql"})
    public void shouldRemoveOrdersAndDelivery() throws Exception {

        final String insert = this.readFileAsString("files/input/order/insert-order.json");

        this.mockMvc.perform(post("/orders")
                        .content(insert)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order.json")))
                .andExpect(status().isCreated());

        final String insert2 = this.readFileAsString("files/input/order/insert-2-order.json");

        this.mockMvc.perform(post("/orders")
                        .content(insert2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order-2.json")))
                .andExpect(status().isCreated());

        this.mockMvc.perform(delete("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        this.mockMvc.perform(delete("/orders/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
