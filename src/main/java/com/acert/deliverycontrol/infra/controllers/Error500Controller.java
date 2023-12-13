package com.acert.deliverycontrol.infra.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
@Slf4j
public class Error500Controller {

    @GetMapping
    public void generateError500() throws Exception {
        Error500Controller.log.error("Error 500");
        throw new Exception("Error 500");
    }

    @GetMapping("/80")
    public void generate80PercentError500() throws Exception {
        final double randomValue = Math.random();
        if (randomValue < 0.8) {
            Error500Controller.log.error("Error 500");
            throw new Exception("Error 500");
        }
    }

    @GetMapping("/50")
    public void generate50PercentError500() throws Exception {
        final double randomValue = Math.random();
        if (randomValue < 0.5) {
            Error500Controller.log.error("Error 500");
            throw new Exception("Error 500");
        }
    }

    @GetMapping("/25")
    public void generate25PercentError500() throws Exception {
        final double randomValue = Math.random();
        if (randomValue < 0.25) {
            Error500Controller.log.error("Error 500");
            throw new Exception("Error 500");
        }
    }
}
