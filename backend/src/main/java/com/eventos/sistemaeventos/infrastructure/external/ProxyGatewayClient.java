package com.eventos.sistemaeventos.infrastructure.external;

import com.eventos.sistemaeventos.application.dto.catedra.BloquearAsientosRequestDTO;
import com.eventos.sistemaeventos.application.dto.catedra.BloquearAsientosResponseDTO;
import com.eventos.sistemaeventos.application.dto.catedra.EventoCatedraDTO;
import com.eventos.sistemaeventos.application.dto.catedra.EventoResumidoCatedraDTO;
import com.eventos.sistemaeventos.application.dto.catedra.VentaCatedraRequestDTO;
import com.eventos.sistemaeventos.application.dto.catedra.VentaCatedraResponseDTO;
import com.eventos.sistemaeventos.application.dto.catedra.VentaResumidaCatedraDTO;
import com.eventos.sistemaeventos.application.port.external.ProxyGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProxyGatewayClient implements ProxyGateway {
    
    private final RestTemplate restTemplate;
    private final String proxyUrl;
    private final ObjectMapper objectMapper;
    
    public ProxyGatewayClient(
            RestTemplate restTemplate,
            @Value("${proxy.url}") String proxyUrl,
            ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.proxyUrl = proxyUrl;
        this.objectMapper = objectMapper;
    }
    
    public List<EventoResumidoCatedraDTO> listarEventosResumidos() {
        log.info("[BACKEND] Consultando eventos resumidos a través del PROXY");
        
        String url = proxyUrl + "/api/proxy/catedra/eventos-resumidos";
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            List<EventoResumidoCatedraDTO> eventos = response.getBody().stream()
                .map(map -> objectMapper.convertValue(map, EventoResumidoCatedraDTO.class))
                .toList();
            
            log.info("[BACKEND] Eventos resumidos obtenidos del PROXY: {}", eventos.size());
            return eventos;
            
        } catch (HttpStatusCodeException e) {
            throw handleHttpStatusException("[BACKEND] Error consultando eventos resumidos del PROXY", "Error consultando eventos del proxy", e);
        } catch (Exception e) {
            log.error("[BACKEND] Error consultando eventos resumidos del PROXY: {}", e.getMessage(), e);
            throw new RuntimeException("Error consultando eventos del proxy", e);
        }
    }
    
    public List<EventoCatedraDTO> listarEventosCompletos() {
        log.info("[BACKEND] Consultando eventos completos a través del PROXY");
        
        String url = proxyUrl + "/api/proxy/catedra/eventos";
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            List<EventoCatedraDTO> eventos = response.getBody().stream()
                .map(map -> objectMapper.convertValue(map, EventoCatedraDTO.class))
                .toList();
            
            log.info("[BACKEND] Eventos completos obtenidos del PROXY: {}", eventos.size());
            return eventos;
            
        } catch (HttpStatusCodeException e) {
            throw handleHttpStatusException("[BACKEND] Error consultando eventos completos del PROXY", "Error consultando eventos del proxy", e);
        } catch (Exception e) {
            log.error("[BACKEND] Error consultando eventos completos del PROXY: {}", e.getMessage(), e);
            throw new RuntimeException("Error consultando eventos del proxy", e);
        }
    }
    
    public EventoCatedraDTO obtenerEvento(Long eventoId) {
        log.info("[BACKEND] Consultando evento {} a través del PROXY", eventoId);
        
        String url = proxyUrl + "/api/proxy/catedra/evento/" + eventoId;
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            EventoCatedraDTO evento = objectMapper.convertValue(response.getBody(), EventoCatedraDTO.class);
            
            log.info("[BACKEND] Evento {} obtenido del PROXY correctamente", eventoId);
            return evento;
            
        } catch (HttpStatusCodeException e) {
            throw handleHttpStatusException(
                String.format("[BACKEND] Error consultando evento %d del PROXY", eventoId),
                "Error consultando evento del proxy",
                e
            );
        } catch (Exception e) {
            log.error("[BACKEND] Error consultando evento {} del PROXY: {}", eventoId, e.getMessage(), e);
            throw new RuntimeException("Error consultando evento del proxy", e);
        }
    }
    
    public BloquearAsientosResponseDTO bloquearAsientos(BloquearAsientosRequestDTO request) {
        log.info("[BACKEND] Bloqueando {} asientos del evento {} a través del PROXY", 
                request.getAsientos().size(), request.getEventoId());
        
        String url = proxyUrl + "/api/proxy/catedra/bloquear-asientos";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BloquearAsientosRequestDTO> entity = new HttpEntity<>(request, headers);
        if (log.isDebugEnabled()) {
            try {
                log.debug("[BACKEND] Payload bloquear-asientos -> {}", objectMapper.writeValueAsString(request));
            } catch (Exception serializationError) {
                log.debug("[BACKEND] Payload bloquear-asientos (raw DTO) -> {}", request, serializationError);
            }
        }

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            BloquearAsientosResponseDTO responseDTO = objectMapper.convertValue(
                response.getBody(), BloquearAsientosResponseDTO.class);
            
            if (responseDTO.getResultado()) {
                log.info("[BACKEND] Asientos bloqueados exitosamente a través del PROXY");
            } else {
                log.warn("[BACKEND] Bloqueo fallido a través del PROXY: {}", responseDTO.getDescripcion());
            }
            
            return responseDTO;
            
        } catch (HttpStatusCodeException e) {
            throw handleHttpStatusException("[BACKEND] Error bloqueando asientos a través del PROXY", "Error bloqueando asientos en el proxy", e);
        } catch (Exception e) {
            log.error("[BACKEND] Error bloqueando asientos a través del PROXY: {}", e.getMessage(), e);
            throw new RuntimeException("Error bloqueando asientos en el proxy", e);
        }
    }
    
    public VentaCatedraResponseDTO realizarVenta(VentaCatedraRequestDTO request) {
        log.info("[BACKEND] Realizando venta de {} asientos del evento {} a través del PROXY", 
                request.getAsientos().size(), request.getEventoId());
        
        String url = proxyUrl + "/api/proxy/catedra/realizar-venta";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<VentaCatedraRequestDTO> entity = new HttpEntity<>(request, headers);
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            VentaCatedraResponseDTO responseDTO = objectMapper.convertValue(
                response.getBody(), VentaCatedraResponseDTO.class);
            
            if (responseDTO.getResultado()) {
                log.info("[BACKEND] Venta realizada exitosamente a través del PROXY. VentaId: {}", 
                        responseDTO.getVentaId());
            } else {
                log.warn("[BACKEND] Venta fallida a través del PROXY: {}", responseDTO.getDescripcion());
            }
            
            return responseDTO;
            
        } catch (HttpStatusCodeException e) {
            throw handleHttpStatusException("[BACKEND] Error realizando venta a través del PROXY", "Error realizando venta en el proxy", e);
        } catch (Exception e) {
            log.error("[BACKEND] Error realizando venta a través del PROXY: {}", e.getMessage(), e);
            throw new RuntimeException("Error realizando venta en el proxy", e);
        }
    }
    
    public List<VentaResumidaCatedraDTO> listarVentas() {
        log.info("[BACKEND] Consultando ventas a través del PROXY");
        
        String url = proxyUrl + "/api/proxy/catedra/ventas";
        
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            
            List<VentaResumidaCatedraDTO> ventas = response.getBody().stream()
                .map(map -> objectMapper.convertValue(map, VentaResumidaCatedraDTO.class))
                .toList();
            
            log.info("[BACKEND] Ventas obtenidas del PROXY: {}", ventas.size());
            return ventas;
            
        } catch (HttpStatusCodeException e) {
            throw handleHttpStatusException("[BACKEND] Error consultando ventas del PROXY", "Error consultando ventas del proxy", e);
        } catch (Exception e) {
            log.error("[BACKEND] Error consultando ventas del PROXY: {}", e.getMessage(), e);
            throw new RuntimeException("Error consultando ventas del proxy", e);
        }
    }
    
    public VentaCatedraResponseDTO obtenerVenta(Long ventaId) {
        log.info("[BACKEND] Consultando venta {} a través del PROXY", ventaId);
        
        String url = proxyUrl + "/api/proxy/catedra/venta/" + ventaId;
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            VentaCatedraResponseDTO venta = objectMapper.convertValue(
                response.getBody(), VentaCatedraResponseDTO.class);
            
            log.info("[BACKEND] Venta {} obtenida del PROXY correctamente", ventaId);
            return venta;
            
        } catch (HttpStatusCodeException e) {
            throw handleHttpStatusException(
                String.format("[BACKEND] Error consultando venta %d del PROXY", ventaId),
                "Error consultando venta del proxy",
                e
            );
        } catch (Exception e) {
            log.error("[BACKEND] Error consultando venta {} del PROXY: {}", ventaId, e.getMessage(), e);
            throw new RuntimeException("Error consultando venta del proxy", e);
        }
    }

    public Object obtenerAsientosEvento(Long eventoId) {
        log.info("[BACKEND] Consultando asientos del evento {} a través del PROXY", eventoId);

        String url = proxyUrl + "/api/proxy/asientos/" + eventoId;

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Object>() {}
            );

            return response.getBody();

        } catch (HttpStatusCodeException e) {
            throw handleHttpStatusException(
                String.format("[BACKEND] Error consultando asientos del evento %d del PROXY", eventoId),
                "Error consultando asientos del proxy",
                e
            );
        } catch (Exception e) {
            log.error("[BACKEND] Error consultando asientos del evento {} del PROXY: {}", eventoId, e.getMessage(), e);
            throw new RuntimeException("Error consultando asientos del proxy", e);
        }
    }

    private RuntimeException handleHttpStatusException(String logContext, String userMessage, HttpStatusCodeException exception) {
        log.error("{} - status={} body={}", logContext, exception.getStatusCode(), exception.getResponseBodyAsString(), exception);
        return new RuntimeException(userMessage + String.format(" (status %s)", exception.getStatusCode()), exception);
    }
}
