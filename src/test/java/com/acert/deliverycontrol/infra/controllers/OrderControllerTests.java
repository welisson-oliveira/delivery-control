package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.config.AbstractTestsConfig;
import com.acert.deliverycontrol.config.ClearContext;
import com.acert.deliverycontrol.config.mockauth.WithUser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.NestedServletException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUser
@ClearContext
public class OrderControllerTests extends AbstractTestsConfig {

    @Test
    @Sql(scripts = {"classpath:db/order/insert-order.sql"})
    public void shouldReturnAllOrders() throws Exception {
        this.mockMvc.perform(get("/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/order/return-all.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnAllActivatedOrders() throws Exception {
        this.mockMvc.perform(get("/orders/activated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/order/return-all-activated-orders.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnAllDoneOrders() throws Exception {
        this.mockMvc.perform(get("/orders/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-done-order.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnAllCanceledOrders() throws Exception {
        this.mockMvc.perform(get("/orders/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-canceled-order.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/order/insert-order.sql"})
    public void shouldReturnOrderById() throws Exception {
        this.mockMvc.perform(get("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/order/return-order.json")))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnActivatedOrdersById() throws Exception {
        this.mockMvc.perform(get("/orders/clients/1/activated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-all-activated-orders.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/order/insert-all-states-orders.sql"})
    @DisplayName("o acesso deve ser negado pois o id que foi passado não é o mesmo do client que esta no contexto ")
    @WithUser(authorities = {"CLIENT"})
    public void shouldNotReturnActivatedOrdersById() {

        Assertions.assertThrows(NestedServletException.class, () -> this.mockMvc.perform(get("/orders/clients/2/activated")
                .contentType(MediaType.APPLICATION_JSON)).andReturn());

    }

    @Test
    @Sql(scripts = {"classpath:db/order/insert-all-states-orders.sql"})
    @DisplayName("acesso permitido pois apesar de os id passado nao ser o mesmo do cliente no contexto o usuario tem autoridade de admin ")
    public void shouldReturnActivatedOrdersById2() throws Exception {

        this.mockMvc.perform(get("/orders/clients/2/activated")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test()
    @Sql(scripts = {"classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnDoneOrdersById() throws Exception {
        this.mockMvc.perform(get("/orders/clients/1/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-done-order.json"), true))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = {"classpath:db/order/insert-all-states-orders.sql"})
    public void shouldReturnCanceledOrdersById() throws Exception {
        this.mockMvc.perform(get("/orders/clients/1/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-canceled-order.json"), true))
                .andExpect(status().isOk());
    }

    @Test
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
    @Sql(scripts = {"classpath:db/order/insert-order.sql"})
    public void shouldUpdateOrderById() throws Exception {
        final String update = this.readFileAsString("files/input/order/update-order.json");

        this.mockMvc.perform(put("/orders/1")
                        .content(update)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().json(this.readFileAsString("files/output/order/return-updated-order.json"), true))
                .andExpect(status().isOk());

    }

    @Sql(scripts = {"classpath:db/order/insert-order.sql"})
    @Test
    public void shouldChangeStatusToInProgress() throws Exception {

        this.mockMvc.perform(patch("/orders/1/in-progress")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Sql(scripts = {"classpath:db/order/insert-order.sql"})
    @Test
    public void shouldChangeStatusToCanceled() throws Exception {

        this.mockMvc.perform(patch("/orders/1/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Sql(scripts = {"classpath:db/order/insert-order.sql"})
    @Test
    public void shouldNotChangeStatusToFinished() throws Exception {

        this.mockMvc.perform(patch("/orders/1/finished")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

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
    @Sql(scripts = {"classpath:db/order/insert-order.sql"})
    public void shouldRemoveOrderWithOneDelivery() throws Exception {
        this.mockMvc.perform(delete("/orders/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
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
