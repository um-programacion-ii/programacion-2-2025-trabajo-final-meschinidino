package com.eventos.sistemaeventos.controller;

import com.eventos.sistemaeventos.domain.Venta;
import com.eventos.sistemaeventos.service.VentaService;
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

/**
 * Controlador de ventas
 */
@Slf4j
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Gestión de ventas y compra de entradas")
@SecurityRequirement(name = "basicAuth")
public class VentaController {

    private final VentaService ventaService;

    /**
     * Realiza una venta
     */
    @Operation(
        summary = "Realizar venta de entradas",
        description = "Completa el proceso de compra realizando la venta de los asientos seleccionados. " +
                      "La sesión debe tener asientos bloqueados previamente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Venta procesada (verificar campo 'success' en respuesta)"),
        @ApiResponse(responseCode = "400", description = "Error en la venta"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping("/realizar")
    public ResponseEntity<?> realizarVenta(Authentication authentication) {
        String username = authentication.getName();
        log.info("Procesando venta para usuario {}", username);

        try {
            Venta venta = ventaService.realizarVenta(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", venta.getResultado());
            response.put("ventaId", venta.getId());
            response.put("ventaIdCatedra", venta.getVentaIdCatedra());
            response.put("descripcion", venta.getDescripcion());
            response.put("precioVenta", venta.getPrecioVenta());
            response.put("asientos", venta.getAsientos());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error realizando venta", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Lista las ventas del usuario
     */
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas(Authentication authentication) {
        String username = authentication.getName();
        log.info("Listando ventas para usuario {}", username);

        try {
            List<Venta> ventas = ventaService.listarVentasUsuario(username);
            return ResponseEntity.ok(ventas);
        } catch (Exception e) {
            log.error("Error listando ventas", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene una venta específica
     */
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVenta(
            Authentication authentication,
            @PathVariable Long id) {

        String username = authentication.getName();
        log.info("Obteniendo venta {} para usuario {}", id, username);

        try {
            Venta venta = ventaService.obtenerVenta(id);

            // Verificar que la venta pertenece al usuario
            if (!venta.getUsername().equals(username)) {
                return ResponseEntity.status(403).build();
            }

            return ResponseEntity.ok(venta);

        } catch (Exception e) {
            log.error("Error obteniendo venta", e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Sincroniza ventas desde cátedra (admin endpoint)
     */
    @PostMapping("/sincronizar")
    public ResponseEntity<?> sincronizarVentas() {
        log.info("Sincronizando ventas desde cátedra");

        try {
            ventaService.sincronizarVentas();
            return ResponseEntity.ok().body("Sincronización completada");
        } catch (Exception e) {
            log.error("Error sincronizando ventas", e);
            return ResponseEntity.internalServerError().body("Error en sincronización");
        }
    }
}

