package com.eventos.proxy.controller;

import com.eventos.proxy.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

/**
 * Controlador que hace forward de requests HTTP al backend
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ProxyController {

    @Value("${backend.url}")
    private String backendUrl;

    private final RestTemplate restTemplate;

    public ProxyController() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Forward de GET requests
     */
    @GetMapping("/**")
    public ResponseEntity<String> forwardGet(
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers) {
        
        String path = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullPath = queryString != null ? path + "?" + queryString : path;
        
        log.info("Forwarding GET {} to backend", fullPath);
        
        String url = backendUrl + fullPath;
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                String.class
            );
            log.debug("Backend response: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("Error forwarding to backend: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Error contacting backend: " + e.getMessage());
        }
    }

    /**
     * Forward de POST requests
     */
    @PostMapping("/**")
    public ResponseEntity<String> forwardPost(
            HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestHeader HttpHeaders headers) {
        
        String path = request.getRequestURI();
        log.info("Forwarding POST {} to backend", path);
        
        String url = backendUrl + path;
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            log.debug("Backend response: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("Error forwarding to backend: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Error contacting backend: " + e.getMessage());
        }
    }

    /**
     * Forward de PUT requests
     */
    @PutMapping("/**")
    public ResponseEntity<String> forwardPut(
            HttpServletRequest request,
            @RequestBody(required = false) String body,
            @RequestHeader HttpHeaders headers) {
        
        String path = request.getRequestURI();
        log.info("Forwarding PUT {} to backend", path);
        
        String url = backendUrl + path;
        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.PUT, 
                entity, 
                String.class
            );
            log.debug("Backend response: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("Error forwarding to backend: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Error contacting backend: " + e.getMessage());
        }
    }

    /**
     * Forward de DELETE requests
     */
    @DeleteMapping("/**")
    public ResponseEntity<String> forwardDelete(
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers) {
        
        String path = request.getRequestURI();
        log.info("Forwarding DELETE {} to backend", path);
        
        String url = backendUrl + path;
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.DELETE, 
                entity, 
                String.class
            );
            log.debug("Backend response: {}", response.getStatusCode());
            return response;
        } catch (Exception e) {
            log.error("Error forwarding to backend: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Error contacting backend: " + e.getMessage());
        }
    }
    @Autowired
    private RedisService redisService;

    /**
     * Endpoint específico para consultar asientos desde Redis
     */
    @GetMapping("/asientos/{eventoId}")
    public ResponseEntity<?> obtenerAsientos(@PathVariable Long eventoId) {
        log.info("Consultando asientos del evento {}", eventoId);
        try {
            Map<Object, Object> asientos = redisService.obtenerEstadoAsientos(eventoId);
            return ResponseEntity.ok(asientos);
        } catch (Exception e) {
            log.error("Error consultando Redis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error consultando estado de asientos");
        }
    }
}

