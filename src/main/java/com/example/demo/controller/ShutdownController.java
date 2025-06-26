package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ShutdownController {

    @PostMapping("/shutdown")
    public ResponseEntity<String> shutdown() {
        System.out.println("Shutdown requested from frontend.");
        // Stop the application
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Optional delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.exit(0);
        }).start();
        return ResponseEntity.ok("Shutting down");
    }
}
