package com.eventos.sistemaeventos.controller;

import com.eventos.sistemaeventos.domain.AsientoSesion;
import com.eventos.sistemaeventos.domain.Sesion;
import com.eventos.sistemaeventos.dto.SeleccionAsientosRequestDTO;
import com.eventos.sistemaeventos.dto.catedra.BloquearAsientosResponseDTO;
import com.eventos.sistemaeventos.service.SesionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador de sesiones
 */
@Slf4j
@RestController
@RequestMapping("/api/sesion")
@RequiredArgsConstructor
@Tag(name = "Sesiones", description = "Gestión de sesiones de compra y selección de asientos")
@SecurityRequirement(name = "basicAuth")
public class SesionController {

    private final SesionService sesionService;

    /**
     * Obtiene la sesión actual del usuario
     */
    @GetMapping
    public ResponseEntity<Sesion> obtenerSesion(Authentication authentication) {
        String username = authentication.getName();
        log.info("Obteniendo sesión para usuario {}", username);

        try {
            Sesion sesion = sesionService.obtenerOCrearSesion(username);
            return ResponseEntity.ok(sesion);
        } catch (Exception e) {
            log.error("Error obteniendo sesión", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Actualiza el paso de la sesión
     */
    @PostMapping("/paso")
    public ResponseEntity<Sesion> actualizarPaso(
            Authentication authentication,
            @RequestParam String paso,
            @RequestParam(required = false) Long eventoId) {

        String username = authentication.getName();
        log.info("Actualizando paso a {} para usuario {}", paso, username);

        try {
            Sesion sesion = sesionService.actualizarPaso(username, paso, eventoId);
            return ResponseEntity.ok(sesion);
        } catch (Exception e) {
            log.error("Error actualizando paso", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Selecciona asientos para la sesión
     */
    @PostMapping("/seleccionar-asientos")
    public ResponseEntity<Sesion> seleccionarAsientos(
            Authentication authentication,
            @RequestBody SeleccionAsientosRequestDTO request) {

        String username = authentication.getName();
        log.info("Seleccionando {} asientos para usuario {}",
                request.getAsientos().size(), username);

        try {
            // Convertir DTO a entidades
            List<AsientoSesion> asientos = request.getAsientos().stream()
                    .map(dto -> {
                        AsientoSesion asiento = new AsientoSesion();
                        asiento.setFila(dto.getFila());
                        asiento.setColumna(dto.getColumna());
                        asiento.setPersona(dto.getPersona());
                        return asiento;
                    })
                    .collect(Collectors.toList());

            // Actualizar evento si viene en el request
            if (request.getEventoId() != null) {
                sesionService.actualizarPaso(username, "SELECCION_ASIENTOS", request.getEventoId());
            }

            Sesion sesion = sesionService.agregarAsientos(username, asientos);
            return ResponseEntity.ok(sesion);

        } catch (Exception e) {
            log.error("Error seleccionando asientos", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Bloquea los asientos seleccionados en cátedra
     */
    @PostMapping("/bloquear-asientos")
    public ResponseEntity<?> bloquearAsientos(Authentication authentication) {
        String username = authentication.getName();
        log.info("Bloqueando asientos para usuario {}", username);

        try {
            BloquearAsientosResponseDTO response = sesionService.bloquearAsientos(username);

            Map<String, Object> result = new HashMap<>();
            result.put("success", response.getResultado());
            result.put("descripcion", response.getDescripcion());
            result.put("asientos", response.getAsientos());

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Error bloqueando asientos", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Elimina la sesión del usuario
     */
    @DeleteMapping
    public ResponseEntity<?> eliminarSesion(Authentication authentication) {
        String username = authentication.getName();
        log.info("Eliminando sesión para usuario {}", username);

        try {
            sesionService.eliminarSesion(username);
            return ResponseEntity.ok().body("Sesión eliminada");
        } catch (Exception e) {
            log.error("Error eliminando sesión", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

