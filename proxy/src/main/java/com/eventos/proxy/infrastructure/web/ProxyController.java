package com.eventos.proxy.infrastructure.web;

import com.eventos.proxy.application.port.in.CatedraProxyUseCase;
import com.eventos.proxy.application.port.in.RedisQueryUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private final RedisQueryUseCase redisQueryUseCase;
    private final CatedraProxyUseCase catedraProxyUseCase;

    public ProxyController(RedisQueryUseCase redisQueryUseCase, CatedraProxyUseCase catedraProxyUseCase) {
        this.redisQueryUseCase = redisQueryUseCase;
        this.catedraProxyUseCase = catedraProxyUseCase;
    }

    @GetMapping("/asientos/{eventoId}")
    public ResponseEntity<?> obtenerAsientos(@PathVariable Long eventoId) {
        log.info("Consultando asientos del evento {}", eventoId);
        try {
            Map<Object, Object> asientos = redisQueryUseCase.obtenerEstadoAsientos(eventoId);
            return ResponseEntity.ok(asientos);
        } catch (Exception e) {
            log.error("Error consultando Redis: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error consultando estado de asientos"));
        }
    }

    @GetMapping("/asientos/{eventoId}/{fila}/{columna}")
    public ResponseEntity<?> obtenerAsiento(
            @PathVariable Long eventoId,
            @PathVariable int fila,
            @PathVariable int columna) {
        log.info("Consultando asiento [{},{}] del evento {}", fila, columna, eventoId);
        try {
            String estado = redisQueryUseCase.obtenerEstadoAsiento(eventoId, fila, columna);
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

    @GetMapping("/catedra/eventos-resumidos")
    public ResponseEntity<List<Map<String, Object>>> obtenerEventosResumidos() {
        log.info("[PROXY] Forwarding request: eventos-resumidos");
        try {
            return ResponseEntity.ok(catedraProxyUseCase.obtenerEventosResumidos());
        } catch (Exception e) {
            log.error("[PROXY] Error en eventos-resumidos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/catedra/eventos")
    public ResponseEntity<List<Map<String, Object>>> obtenerEventosCompletos() {
        log.info("[PROXY] Forwarding request: eventos completos");
        try {
            return ResponseEntity.ok(catedraProxyUseCase.obtenerEventosCompletos());
        } catch (Exception e) {
            log.error("[PROXY] Error en eventos completos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/catedra/evento/{eventoId}")
    public ResponseEntity<Map<String, Object>> obtenerEvento(@PathVariable Long eventoId) {
        log.info("[PROXY] Forwarding request: evento {}", eventoId);
        try {
            return ResponseEntity.ok(catedraProxyUseCase.obtenerEvento(eventoId));
        } catch (Exception e) {
            log.error("[PROXY] Error obteniendo evento {}: {}", eventoId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/catedra/bloquear-asientos")
    public ResponseEntity<Map<String, Object>> bloquearAsientos(@RequestBody Map<String, Object> request) {
        log.info("[PROXY] Forwarding request: bloquear asientos");
        try {
            return ResponseEntity.ok(catedraProxyUseCase.bloquearAsientos(request));
        } catch (Exception e) {
            log.error("[PROXY] Error bloqueando asientos: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/catedra/realizar-venta")
    public ResponseEntity<Map<String, Object>> realizarVenta(@RequestBody Map<String, Object> request) {
        log.info("[PROXY] Forwarding request: realizar venta");
        try {
            return ResponseEntity.ok(catedraProxyUseCase.realizarVenta(request));
        } catch (Exception e) {
            log.error("[PROXY] Error realizando venta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/catedra/ventas")
    public ResponseEntity<List<Map<String, Object>>> listarVentas() {
        log.info("[PROXY] Forwarding request: listar ventas");
        try {
            return ResponseEntity.ok(catedraProxyUseCase.listarVentas());
        } catch (Exception e) {
            log.error("[PROXY] Error listando ventas: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/catedra/venta/{ventaId}")
    public ResponseEntity<Map<String, Object>> obtenerVenta(@PathVariable Long ventaId) {
        log.info("[PROXY] Forwarding request: venta {}", ventaId);
        try {
            return ResponseEntity.ok(catedraProxyUseCase.obtenerVenta(ventaId));
        } catch (Exception e) {
            log.error("[PROXY] Error obteniendo venta {}: {}", ventaId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
