package com.acert.deliverycontrol.config;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;


@AutoConfigureMockMvc
@SpringBootTest
public abstract class AbstractTestsConfig extends TestContainerConfig {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }


    protected String readFileAsString(final String filePath) throws IOException {
        final ClassLoader classLoader = this.getClass().getClassLoader();
        return IOUtils.toString(classLoader.getResourceAsStream(filePath));
    }

}

