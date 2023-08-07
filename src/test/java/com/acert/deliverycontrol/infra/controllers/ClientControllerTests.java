package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.config.AbstractTestsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ClientControllerTests extends AbstractTestsConfig {

    @Test
    @Sql(scripts = "classpath:db/client/insert-client.sql")
    public void shouldReturnAllClients() throws Exception {
        this.mockMvc.perform(get("/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/client/return-all.json")))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = "classpath:db/client/insert-client.sql")
    public void shouldReturnClientById() throws Exception {
        final String update = this.readFileAsString("files/input/client/update-client.json");

        this.mockMvc.perform(get("/clients/1")
                        .content(update)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/client/return-client.json")))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateClient() throws Exception {
        final String insert = this.readFileAsString("files/input/client/insert-client.json");

        this.mockMvc.perform(post("/clients")
                        .content(insert)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/client/return-client.json")))
                .andExpect(status().isCreated());
    }

    @Test
    @Sql(scripts = "classpath:db/client/insert-client.sql")
    public void shouldUpdateClient() throws Exception {
        final String update = this.readFileAsString("files/input/client/update-client.json");

        this.mockMvc.perform(put("/clients/1")
                        .content(update)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/client/return-updated-client.json")))
                .andExpect(status().isOk());
    }

    @Test
    @Sql(scripts = "classpath:db/client/insert-client.sql")
    public void shouldRemoveClient() throws Exception {
        this.mockMvc.perform(delete("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
