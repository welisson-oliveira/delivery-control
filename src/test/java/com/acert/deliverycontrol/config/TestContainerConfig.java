package com.acert.deliverycontrol.config;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

@AutoConfigureTestDatabase(
        replace = AutoConfigureTestDatabase.Replace.NONE
)
@Testcontainers
public abstract class TestContainerConfig {
    public static GenericContainer<?> firebirdSQLContainer;

    public TestContainerConfig() {
        if (Objects.isNull(TestContainerConfig.firebirdSQLContainer)) {
            TestContainerConfig.firebirdSQLContainer = new GenericContainer("jacobalberty/firebird")
                    .withExposedPorts(3050)
                    .withEnv("FIREBIRD_DATABASE", "test")
                    .withEnv("FIREBIRD_USER", "test")
                    .withEnv("FIREBIRD_PASSWORD", "test")
                    .waitingFor(Wait.forListeningPort());


            TestContainerConfig.firebirdSQLContainer.start();
        }

        final String jdbcUrl = String.format("jdbc:firebirdsql://localhost:%s/test?charSet=utf-8",
                AbstractTestsConfig.firebirdSQLContainer.getMappedPort(3050));
        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.datasource.username", "test");
        System.setProperty("spring.datasource.password", "test");

    }
}
