package com.acert.deliverycontrol.infra.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/stress")
public class MemoryAndCPUStressController {

    private List<Object> memoryList = new ArrayList<>();

    private boolean memory = true;
    private boolean cpu = true;

    private final List<Thread> threads = new ArrayList<>();

    @GetMapping("/memory-on")
    public void stressOn() {
        this.memory = true;
        try {
            while (this.memory) {
                this.memoryList.add(new byte[1024 * 1024]);
            }
        } catch (final OutOfMemoryError e) {
            MemoryAndCPUStressController.log.error("Memory stress test completed. OutOfMemoryError occurred.");
        }
    }

    @GetMapping("/memory-off")
    public void stressOff() {
        this.memory = false;
        this.memoryList = new ArrayList<>();
        MemoryAndCPUStressController.log.info("the memory has been cleared");
    }

    @GetMapping("/cpu-on")
    public void stressCPUOn() {
        this.cpu = true;
        while (this.cpu) {
            final Thread thread = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    final double result = Math.sqrt(j * Math.random());
                }
            });
            thread.start();
            this.threads.add(thread);
        }

    }

    @GetMapping("/cpu-off")
    public void stressCPUOff() {
        this.cpu = false;
        this.threads.forEach(Thread::interrupt);
        this.threads.clear();
        MemoryAndCPUStressController.log.info("CPU stress test completed");
    }
}
