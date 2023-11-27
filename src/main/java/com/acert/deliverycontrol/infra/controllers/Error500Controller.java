package com.acert.deliverycontrol.infra.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class Error500Controller {

    @GetMapping("/80")
    public void generate80PercentError500() throws Exception {
        double randomValue = Math.random();
        if(randomValue < 0.8) {
            throw new Exception("Error 500");
        }
    }

    @GetMapping("/50")
    public void generate50PercentError500() throws Exception {
        double randomValue = Math.random();
        if(randomValue < 0.5) {
            throw new Exception("Error 500");
        }
    }

    @GetMapping("/25")
    public void generate25PercentError500() throws Exception {
        double randomValue = Math.random();
        if(randomValue < 0.25) {
            throw new Exception("Error 500");
        }
    }
}
