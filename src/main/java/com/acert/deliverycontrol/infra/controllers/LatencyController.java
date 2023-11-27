package com.acert.deliverycontrol.infra.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/latency")
public class LatencyController {

    @GetMapping("/1m")
    public String healthCheck() throws InterruptedException {
        Thread.sleep(60000); // 1 minuto
        return "[1m]\n";
    }

    @GetMapping("/30s")
    public String readinessCheck() throws InterruptedException {
        Thread.sleep(30000); // 30 segundos
        return "[30s]\n";
    }

    @GetMapping("/10s")
    public String startupCheck() throws InterruptedException {
        Thread.sleep(10000); // 10 segundos
        return "[10s]\n";
    }

}
