package com.acert.deliverycontrol;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DeliveryControlApplication {

    public static void main(final String[] args) {
        SpringApplication.run(DeliveryControlApplication.class, args);
        DeliveryControlApplication.log.info("Applicação inicializada com sucesso");
    }

}
