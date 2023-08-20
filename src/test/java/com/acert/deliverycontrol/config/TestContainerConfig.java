package com.acert.deliverycontrol.config;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;

@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Testcontainers
public abstract class TestContainerConfig {
    public static PostgreSQLContainer<?> postgresDBContainer;
    public static GenericContainer<?> redisContainer;

    public TestContainerConfig() {
        if (Objects.isNull(TestContainerConfig.postgresDBContainer)) {
            TestContainerConfig.postgresDBContainer = new PostgreSQLContainer<>("postgres:9.5");
            TestContainerConfig.postgresDBContainer.start();

            final String jdbcUrl = String.format("jdbc:postgresql://localhost:%s/mapping",
                    TestContainerConfig.postgresDBContainer.getMappedPort(5432).toString());

            System.setProperty("spring.datasource.url", TestContainerConfig.postgresDBContainer.getJdbcUrl());
            System.setProperty("spring.datasource.username", TestContainerConfig.postgresDBContainer.getUsername());
            System.setProperty("spring.datasource.password", TestContainerConfig.postgresDBContainer.getPassword());

        }

        if (Objects.isNull(AbstractTestsConfig.redisContainer)) {
            TestContainerConfig.redisContainer = new GenericContainer<>(DockerImageName.parse("redis:5.0-rc")).withExposedPorts(6379)
                    .withReuse(true);
            TestContainerConfig.redisContainer.start();

            System.setProperty("spring.redis.host", "localhost");
            System.setProperty("spring.redis.port", AbstractTestsConfig.redisContainer.getMappedPort(6379).toString());
        }

    }
}
