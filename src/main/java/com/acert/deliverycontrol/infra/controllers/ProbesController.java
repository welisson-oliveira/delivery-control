package com.acert.deliverycontrol.infra.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProbesController {

    @GetMapping("/health-check")
    public String healthCheck() {
        return "Is hHealthy!\n";
    }

    @GetMapping("/readiness-check")
    public String readinessCheck() {
        return "Is Ready!\n";
    }

    @GetMapping("/startup-check")
    public String startupCheck() {
        return "Initialization Completed!\n";
    }

}
