package com.acert.deliverycontrol.infra.controllers;

import com.acert.deliverycontrol.config.AbstractTestsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest

public class ClientTest extends AbstractTestsConfig {


    @Test
    @Sql(scripts = "classpath:db/insert-client.sql")
    public void getAll() throws Exception {
        this.mockMvc.perform(get("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .contextPath(""))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
