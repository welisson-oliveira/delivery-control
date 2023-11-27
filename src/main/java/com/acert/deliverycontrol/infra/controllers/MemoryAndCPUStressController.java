package com.acert.deliverycontrol.infra.controllers;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Log4j2
@RequestMapping("/stress")
public class MemoryAndCPUStressController {

    private List<Object> memoryList = new ArrayList<>();

    private boolean memory = true;
    private boolean cpu = true;

    private List<Thread> threads = new ArrayList<>();

    @GetMapping("/memory-on")
    public void stressOn() {
        memory = true;
        try {
            while (memory) {
                memoryList.add(new byte[1024 * 1024]);
            }
        } catch (OutOfMemoryError e) {
            log.fatal("Memory stress test completed. OutOfMemoryError occurred.");
        }
    }

    @GetMapping("/memory-off")
    public void stressOff() {
        memory = false;
        memoryList = new ArrayList<>();
        log.info("the memory has been cleared");
    }

    @GetMapping("/cpu-on")
    public void stressCPUOn() {
        cpu = true;
        while (cpu) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    double result = Math.sqrt(j * Math.random());
                }
            });
            thread.start();
            threads.add(thread);
        }

    }

    @GetMapping("/cpu-off")
    public void stressCPUOff() {
        cpu = false;
        threads.forEach(Thread::interrupt);
        threads.clear();
        log.info("CPU stress test completed");
    }
}
