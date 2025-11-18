package com.eventos.sistemaeventos.service;

import com.eventos.sistemaeventos.dto.catedra.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Servicio para consumir endpoints del servicio de cátedra
 * Referencia: Sección 5.1 del documento
 */
@Slf4j
@Service
public class CatedraService {
    
    private final RestTemplate restTemplate;
    private final String catedraUrl;
    private final String catedraToken;
    
    public CatedraService(
            RestTemplate restTemplate,
            @Value("${catedra.url}") String catedraUrl,
            @Value("${catedra.token}") String catedraToken) {
        this.restTemplate = restTemplate;
        this.catedraUrl = catedraUrl;
        this.catedraToken = catedraToken;
    }
    
    /**
     * Crea headers con autenticación
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(catedraToken);
        return headers;
    }
    
    /**
     * Listado resumido de eventos activos
     * Endpoint: GET /api/endpoints/v1/eventos-resumidos
     * Payload 3
     */
    public List<EventoResumidoCatedraDTO> listarEventosResumidos() {
        log.info("Consultando eventos resumidos desde cátedra");
        
        String url = catedraUrl + "/api/endpoints/v1/eventos-resumidos";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<List<EventoResumidoCatedraDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<EventoResumidoCatedraDTO>>() {}
            );
            
            log.info("Eventos resumidos obtenidos: {}", response.getBody().size());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error consultando eventos resumidos: {}", e.getMessage());
            throw new RuntimeException("Error consultando eventos de cátedra", e);
        }
    }
    
    /**
     * Listado completo de eventos activos
     * Endpoint: GET /api/endpoints/v1/eventos
     * Payload 4
     */
    public List<EventoCatedraDTO> listarEventosCompletos() {
        log.info("Consultando eventos completos desde cátedra");
        
        String url = catedraUrl + "/api/endpoints/v1/eventos";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<List<EventoCatedraDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<EventoCatedraDTO>>() {}
            );
            
            log.info("Eventos completos obtenidos: {}", response.getBody().size());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error consultando eventos completos: {}", e.getMessage());
            throw new RuntimeException("Error consultando eventos de cátedra", e);
        }
    }
    
    /**
     * Obtener datos completos de un evento
     * Endpoint: GET /api/endpoints/v1/evento/{id}
     * Payload 5
     */
    public EventoCatedraDTO obtenerEvento(Long eventoId) {
        log.info("Consultando evento {} desde cátedra", eventoId);
        
        String url = catedraUrl + "/api/endpoints/v1/evento/" + eventoId;
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<EventoCatedraDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                EventoCatedraDTO.class
            );
            
            log.info("Evento {} obtenido correctamente", eventoId);
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error consultando evento {}: {}", eventoId, e.getMessage());
            throw new RuntimeException("Error consultando evento de cátedra", e);
        }
    }
    
    /**
     * Bloquear asientos de un evento
     * Endpoint: POST /api/endpoints/v1/bloquear-asientos
     * Payload 6
     */
    public BloquearAsientosResponseDTO bloquearAsientos(BloquearAsientosRequestDTO request) {
        log.info("Bloqueando {} asientos del evento {}", 
                request.getAsientos().size(), request.getEventoId());
        
        String url = catedraUrl + "/api/endpoints/v1/bloquear-asientos";
        HttpEntity<BloquearAsientosRequestDTO> entity = new HttpEntity<>(request, createHeaders());
        
        try {
            ResponseEntity<BloquearAsientosResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                BloquearAsientosResponseDTO.class
            );
            
            BloquearAsientosResponseDTO responseBody = response.getBody();
            
            if (responseBody.getResultado()) {
                log.info("Asientos bloqueados exitosamente");
            } else {
                log.warn("Bloqueo fallido: {}", responseBody.getDescripcion());
            }
            
            return responseBody;
            
        } catch (Exception e) {
            log.error("Error bloqueando asientos: {}", e.getMessage());
            throw new RuntimeException("Error bloqueando asientos en cátedra", e);
        }
    }
    
    /**
     * Realizar venta de asientos
     * Endpoint: POST /api/endpoints/v1/realizar-venta
     * Payload 7
     */
    public VentaCatedraResponseDTO realizarVenta(VentaCatedraRequestDTO request) {
        log.info("Realizando venta de {} asientos del evento {}", 
                request.getAsientos().size(), request.getEventoId());
        
        String url = catedraUrl + "/api/endpoints/v1/realizar-venta";
        HttpEntity<VentaCatedraRequestDTO> entity = new HttpEntity<>(request, createHeaders());
        
        try {
            ResponseEntity<VentaCatedraResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                VentaCatedraResponseDTO.class
            );
            
            VentaCatedraResponseDTO responseBody = response.getBody();
            
            if (responseBody.getResultado()) {
                log.info("Venta realizada exitosamente. VentaId: {}", responseBody.getVentaId());
            } else {
                log.warn("Venta fallida: {}", responseBody.getDescripcion());
            }
            
            return responseBody;
            
        } catch (Exception e) {
            log.error("Error realizando venta: {}", e.getMessage());
            throw new RuntimeException("Error realizando venta en cátedra", e);
        }
    }
    
    /**
     * Listar todas las ventas del alumno (resumido)
     * Endpoint: GET /api/endpoints/v1/listar-ventas
     * Payload 8
     */
    public List<VentaResumidaCatedraDTO> listarVentas() {
        log.info("Consultando ventas desde cátedra");
        
        String url = catedraUrl + "/api/endpoints/v1/listar-ventas";
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<List<VentaResumidaCatedraDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<VentaResumidaCatedraDTO>>() {}
            );
            
            log.info("Ventas obtenidas: {}", response.getBody().size());
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error consultando ventas: {}", e.getMessage());
            throw new RuntimeException("Error consultando ventas de cátedra", e);
        }
    }
    
    /**
     * Obtener detalle de una venta específica
     * Endpoint: GET /api/endpoints/v1/listar-venta/{id}
     * Payload 9
     */
    public VentaCatedraResponseDTO obtenerVenta(Long ventaId) {
        log.info("Consultando venta {} desde cátedra", ventaId);
        
        String url = catedraUrl + "/api/endpoints/v1/listar-venta/" + ventaId;
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        
        try {
            ResponseEntity<VentaCatedraResponseDTO> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                VentaCatedraResponseDTO.class
            );
            
            log.info("Venta {} obtenida correctamente", ventaId);
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error consultando venta {}: {}", ventaId, e.getMessage());
            throw new RuntimeException("Error consultando venta de cátedra", e);
        }
    }
}

