package com.acert.deliverycontrol.infra.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LifeCycleApplication {

    @GetMapping("/post-start")
    public void postStart() {
        System.out.println("Iniciou");
    }


    @GetMapping("/pre-stop")
    public void preStop() {
        System.out.println("Vai finalizar");
    }
}
