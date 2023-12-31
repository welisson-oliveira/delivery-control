package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.config.AbstractTestsConfig;
import com.acert.deliverycontrol.config.ClearContext;
import com.acert.deliverycontrol.config.mockauth.WithUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithUser
@ClearContext
class ClientControllerTests extends AbstractTestsConfig {

    @Test
    void shouldLogin() throws Exception {
        final String insert = "{\n" +
                "    \"username\":\"ADMIN@EMAIL.COM\",\n" +
                "    \"password\":\"admin\"\n" +
                "}";

        this.mockMvc.perform(post("/clients/login")
                        .content(insert)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnAllClients() throws Exception {
        this.mockMvc.perform(get("/clients")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/client/return-all.json")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnClientById() throws Exception {

        this.mockMvc.perform(get("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/client/return-client.json")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotReturnClientByNonexistentId() throws Exception {
        this.mockMvc.perform(get("/clients/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnRolesByClientId() throws Exception {

        this.mockMvc.perform(get("/clients/1/roles")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(content().json("[{\"id\":1,\"authority\":\"ADMIN\"},{\"id\":2,\"authority\":\"CLIENT\"}]"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateClient() throws Exception {
        final String insert = this.readFileAsString("files/input/client/insert-client.json");

        this.mockMvc.perform(post("/clients")
                        .content(insert)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/client/return-created-client.json")))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateClient() throws Exception {
        final String update = this.readFileAsString("files/input/client/update-client.json");

        this.mockMvc.perform(put("/clients/1")
                        .content(update)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(this.readFileAsString("files/output/client/return-updated-client.json")))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRemoveClient() throws Exception {
        this.mockMvc.perform(delete("/clients/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
