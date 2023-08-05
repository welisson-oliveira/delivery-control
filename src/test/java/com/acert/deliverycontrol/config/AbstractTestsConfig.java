package com.acert.deliverycontrol.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Testcontainers
@AutoConfigureMockMvc
public abstract class AbstractTestsConfig {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Container
    public static GenericContainer<?> firebirdSQLContainer = new GenericContainer("jacobalberty/firebird")
            .withExposedPorts(3050)
            .withEnv("FIREBIRD_DATABASE", "test")
            .withEnv("FIREBIRD_USER", "test")
            .withEnv("FIREBIRD_PASSWORD", "test")
            .waitingFor(Wait.forListeningPort());

    @BeforeAll
    static void setUp() {
        final String jdbcUrl = String.format("jdbc:firebirdsql://localhost:%s/test?charSet=utf-8",
                AbstractTestsConfig.firebirdSQLContainer.getMappedPort(3050));
        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.datasource.username", "test");
        System.setProperty("spring.datasource.password", "test");
    }

}

