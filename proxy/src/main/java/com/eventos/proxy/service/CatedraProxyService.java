package com.eventos.proxy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Servicio proxy para comunicación con API de cátedra
 * Este servicio actúa como intermediario entre el backend y la cátedra
 */
@Slf4j
@Service
public class CatedraProxyService {
    
    private final RestTemplate restTemplate;
    private final String catedraUrl;
    private final String catedraToken;
    
    public CatedraProxyService(
            RestTemplate restTemplate,
            @Value("${catedra.url}") String catedraUrl,
            @Value("${catedra.token}") String catedraToken) {
        this.restTemplate = restTemplate;
        this.catedraUrl = catedraUrl;
        this.catedraToken = catedraToken;
    }
    
    /**
     * Crea headers con autenticación para cátedra
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(catedraToken);
        return headers;
    }
    
    /**
     * Obtener eventos resumidos desde cátedra
     */
    public ResponseEntity<List<Map<String, Object>>> obtenerEventosResumidos() {
        log.info("[PROXY] Obteniendo eventos resumidos desde cátedra");
        
        String url = catedraUrl + "/api/endpoints/v1/eventos-resumidos";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            log.info("[PROXY] Eventos resumidos obtenidos exitosamente: {} eventos", 
                    response.getBody() != null ? response.getBody().size() : 0);
            return response;
            
        } catch (Exception e) {
            log.error("[PROXY] Error obteniendo eventos resumidos: {}", e.getMessage());
            throw new RuntimeException("Error consultando eventos resumidos de cátedra", e);
        }
    }
    
    /**
     * Obtener eventos completos desde cátedra
     */
    public ResponseEntity<List<Map<String, Object>>> obtenerEventosCompletos() {
        log.info("[PROXY] Obteniendo eventos completos desde cátedra");
        
        String url = catedraUrl + "/api/endpoints/v1/eventos";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            log.info("[PROXY] Eventos completos obtenidos exitosamente: {} eventos", 
                    response.getBody() != null ? response.getBody().size() : 0);
            return response;
            
        } catch (Exception e) {
            log.error("[PROXY] Error obteniendo eventos completos: {}", e.getMessage());
            throw new RuntimeException("Error consultando eventos completos de cátedra", e);
        }
    }
    
    /**
     * Obtener un evento específico desde cátedra
     */
    public ResponseEntity<Map<String, Object>> obtenerEvento(Long eventoId) {
        log.info("[PROXY] Obteniendo evento {} desde cátedra", eventoId);
        
        String url = catedraUrl + "/api/endpoints/v1/evento/" + eventoId;
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            log.info("[PROXY] Evento {} obtenido exitosamente", eventoId);
            return response;
            
        } catch (Exception e) {
            log.error("[PROXY] Error obteniendo evento {}: {}", eventoId, e.getMessage());
            throw new RuntimeException("Error consultando evento de cátedra", e);
        }
    }
    
    /**
     * Bloquear asientos en cátedra
     */
    public ResponseEntity<Map<String, Object>> bloquearAsientos(Map<String, Object> request) {
        log.info("[PROXY] Bloqueando asientos en cátedra");
        
        String url = catedraUrl + "/api/endpoints/v1/bloquear-asientos";
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            log.info("[PROXY] Asientos bloqueados exitosamente");
            return response;
            
        } catch (Exception e) {
            log.error("[PROXY] Error bloqueando asientos: {}", e.getMessage());
            throw new RuntimeException("Error bloqueando asientos en cátedra", e);
        }
    }
    
    /**
     * Realizar venta en cátedra
     */
    public ResponseEntity<Map<String, Object>> realizarVenta(Map<String, Object> request) {
        log.info("[PROXY] Realizando venta en cátedra");
        
        String url = catedraUrl + "/api/endpoints/v1/realizar-venta";
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createHeaders());
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            log.info("[PROXY] Venta realizada exitosamente");
            return response;
            
        } catch (Exception e) {
            log.error("[PROXY] Error realizando venta: {}", e.getMessage());
            throw new RuntimeException("Error realizando venta en cátedra", e);
        }
    }
    
    /**
     * Listar ventas desde cátedra
     */
    public ResponseEntity<List<Map<String, Object>>> listarVentas() {
        log.info("[PROXY] Listando ventas desde cátedra");
        
        String url = catedraUrl + "/api/endpoints/v1/listar-ventas";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            log.info("[PROXY] Ventas obtenidas exitosamente: {} ventas", 
                    response.getBody() != null ? response.getBody().size() : 0);
            return response;
            
        } catch (Exception e) {
            log.error("[PROXY] Error listando ventas: {}", e.getMessage());
            throw new RuntimeException("Error listando ventas de cátedra", e);
        }
    }
    
    /**
     * Obtener una venta específica desde cátedra
     */
    public ResponseEntity<Map<String, Object>> obtenerVenta(Long ventaId) {
        log.info("[PROXY] Obteniendo venta {} desde cátedra", ventaId);
        
        String url = catedraUrl + "/api/endpoints/v1/listar-venta/" + ventaId;
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            log.info("[PROXY] Venta {} obtenida exitosamente", ventaId);
            return response;
            
        } catch (Exception e) {
            log.error("[PROXY] Error obteniendo venta {}: {}", ventaId, e.getMessage());
            throw new RuntimeException("Error consultando venta de cátedra", e);
        }
    }
}

