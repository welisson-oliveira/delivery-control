package com.acert.deliverycontrol.infra.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProbesController {

    @GetMapping("/health-check")
    public String healthCheck() {
        return "is healthy";
    }

    @GetMapping("/readiness-check")
    public String readinessCheck() {
        return "is ready";
    }

    @GetMapping("/startup-check")
    public String startupCheck() {
        return "initialization completed";
    }

}
