package com.acert.deliverycontrol.infra.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LoggingController {

    private static final Logger log = LogManager.getLogger(LoggingController.class);

    @GetMapping

    public String generateLogs() {
        LoggingController.log.debug("A DEBUG Message");
        LoggingController.log.info("An INFO Message");
        LoggingController.log.warn("A WARN Message");
        LoggingController.log.error("An ERROR Message");
        LoggingController.log.fatal("A FATAL Message");
        LoggingController.log.trace("A TRACE Message");

        return "Confira os logs para ver o resultado";
    }
}
