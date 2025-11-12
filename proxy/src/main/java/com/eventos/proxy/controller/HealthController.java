package com.eventos.proxy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health check endpoint
 */
@Slf4j
@RestController
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.debug("Health check");
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "eventos-proxy");
        return ResponseEntity.ok(response);
    }
}

