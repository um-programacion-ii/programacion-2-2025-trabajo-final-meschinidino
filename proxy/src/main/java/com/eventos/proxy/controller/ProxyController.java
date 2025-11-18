package com.eventos.proxy.controller;

import com.eventos.proxy.service.CatedraProxyService;
import com.eventos.proxy.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador proxy para comunicación con cátedra y consulta de Redis
 *
 * Este controlador actúa como intermediario entre el backend y los servicios de cátedra.
 * Expone endpoints específicos para:
 * - Consultar eventos desde cátedra
 * - Consultar estado de asientos desde Redis
 * - Bloquear asientos y realizar ventas en cátedra
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ProxyController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private CatedraProxyService catedraProxyService;

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
                    .body(Map.of("error", "Error consultando estado de asientos"));
        }
    }

    /**
     * Endpoint para consultar un asiento específico
     */
    @GetMapping("/asientos/{eventoId}/{fila}/{columna}")
    public ResponseEntity<?> obtenerAsiento(
            @PathVariable Long eventoId,
            @PathVariable int fila,
            @PathVariable int columna) {
        log.info("Consultando asiento [{},{}] del evento {}", fila, columna, eventoId);
        try {
            String estado = redisService.obtenerEstadoAsiento(eventoId, fila, columna);
            return ResponseEntity.ok(Map.of(
                "eventoId", eventoId,
                "fila", fila,
                "columna", columna,
                "estado", estado
            ));
        } catch (Exception e) {
            log.error("Error consultando Redis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error consultando asiento"));
        }
    }
}

