package com.eventos.sistemaeventos.presentation.controller;

import com.eventos.sistemaeventos.application.service.EventoService;
import com.eventos.sistemaeventos.domain.Evento;
import com.eventos.sistemaeventos.presentation.dto.EventoDetalleResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
@Tag(name = "Eventos", description = "Gestión de eventos y sincronización con cátedra")
@SecurityRequirement(name = "basicAuth")
public class EventoController {

    private final EventoService eventoService;

    @Operation(
        summary = "Listar eventos activos",
        description = "Retorna todos los eventos activos disponibles para compra. Los eventos están sincronizados con cátedra."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de eventos obtenida"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        log.info("Listando eventos activos");
        List<Evento> eventos = eventoService.listarEventosActivos();
        return ResponseEntity.ok(eventos);
    }

    @Operation(
        summary = "Obtener evento por ID",
        description = "Retorna los detalles completos de un evento específico incluyendo asientos disponibles"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento encontrado"),
        @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EventoDetalleResponseDTO> obtenerEvento(
            @Parameter(description = "ID del evento") @PathVariable Long id) {
        log.info("Obteniendo evento {}", id);
        Evento evento = eventoService.obtenerEvento(id);
        Object asientos = eventoService.obtenerAsientosEvento(id);

        EventoDetalleResponseDTO response = new EventoDetalleResponseDTO();
        response.setEvento(evento);
        response.setAsientos(asientos);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Sincronizar todos los eventos",
        description = "Sincroniza todos los eventos desde la API de cátedra. Endpoint administrativo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sincronización completada"),
        @ApiResponse(responseCode = "500", description = "Error en sincronización"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping("/sincronizar")
    public ResponseEntity<?> sincronizarEventos() {
        log.info("Iniciando sincronización de eventos");

        try {
            eventoService.sincronizarEventos();
            return ResponseEntity.ok().body("Sincronización completada");
        } catch (Exception e) {
            log.error("Error sincronizando eventos", e);
            return ResponseEntity.internalServerError().body("Error en sincronización");
        }
    }

    @Operation(
        summary = "Sincronizar un evento específico",
        description = "Sincroniza un evento particular desde la API de cátedra por su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Evento sincronizado"),
        @ApiResponse(responseCode = "500", description = "Error en sincronización"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping("/{id}/sincronizar")
    public ResponseEntity<Evento> sincronizarEvento(
            @Parameter(description = "ID del evento a sincronizar") @PathVariable Long id) {
        log.info("Sincronizando evento {}", id);

        try {
            Evento evento = eventoService.sincronizarEvento(id);
            return ResponseEntity.ok(evento);
        } catch (Exception e) {
            log.error("Error sincronizando evento {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
