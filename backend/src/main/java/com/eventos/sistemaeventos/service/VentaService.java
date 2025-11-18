package com.eventos.sistemaeventos.service;

import com.eventos.sistemaeventos.domain.AsientoVenta;
import com.eventos.sistemaeventos.domain.Sesion;
import com.eventos.sistemaeventos.domain.Venta;
import com.eventos.sistemaeventos.dto.catedra.VentaCatedraRequestDTO;
import com.eventos.sistemaeventos.dto.catedra.VentaCatedraResponseDTO;
import com.eventos.sistemaeventos.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar ventas
 * Referencia: Sección 4.3 del documento
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VentaService {
    
    private final VentaRepository ventaRepository;
    private final SesionService sesionService;
    private final EventoService eventoService;
    private final CatedraService catedraService;
    
    /**
     * Realiza una venta de entradas
     */
    @Transactional
    public Venta realizarVenta(String username) {
        log.info("Procesando venta para usuario {}", username);
        
        // Obtener sesión con asientos seleccionados
        Sesion sesion = sesionService.obtenerSesion(username);
        
        if (sesion.getEventoId() == null || sesion.getAsientosSeleccionados().isEmpty()) {
            throw new RuntimeException("No hay asientos seleccionados para vender");
        }
        
        // Obtener evento para precio
        var evento = eventoService.obtenerEvento(sesion.getEventoId());
        
        // Preparar request para cátedra
        VentaCatedraRequestDTO request = new VentaCatedraRequestDTO();
        request.setEventoId(sesion.getEventoId());
        request.setFecha(LocalDateTime.now());
        request.setPrecioVenta(evento.getPrecioEntrada());
        request.setAsientos(
            sesion.getAsientosSeleccionados().stream()
                .map(a -> new VentaCatedraRequestDTO.AsientoVentaDTO(
                    a.getFila(), 
                    a.getColumna(), 
                    a.getPersona()
                ))
                .collect(Collectors.toList())
        );
        
        // Realizar venta en cátedra
        VentaCatedraResponseDTO response = catedraService.realizarVenta(request);
        
        // Crear registro local de venta
        Venta venta = new Venta();
        venta.setEventoId(response.getEventoId());
        venta.setVentaIdCatedra(response.getVentaId());
        venta.setFechaVenta(response.getFechaVenta());
        venta.setPrecioVenta(response.getPrecioVenta());
        venta.setResultado(response.getResultado());
        venta.setDescripcion(response.getDescripcion());
        venta.setUsername(username);
        venta.setEstadoSincronizacion(response.getResultado() ? "CONFIRMADA" : "FALLIDA");
        
        // Mapear asientos
        if (response.getAsientos() != null) {
            List<AsientoVenta> asientos = response.getAsientos().stream()
                    .map(a -> {
                        AsientoVenta asiento = new AsientoVenta();
                        asiento.setFila(a.getFila());
                        asiento.setColumna(a.getColumna());
                        asiento.setPersona(a.getPersona());
                        asiento.setEstado(a.getEstado());
                        return asiento;
                    })
                    .collect(Collectors.toList());
            
            venta.getAsientos().addAll(asientos);
        }
        
        venta = ventaRepository.save(venta);
        
        // Limpiar sesión después de venta
        sesionService.eliminarSesion(username);
        
        log.info("Venta completada. ID local: {}, ID cátedra: {}, Resultado: {}", 
                venta.getId(), venta.getVentaIdCatedra(), venta.getResultado());
        
        return venta;
    }
    
    /**
     * Lista las ventas de un usuario
     */
    public List<Venta> listarVentasUsuario(String username) {
        return ventaRepository.findByUsernameOrderByFechaVentaDesc(username);
    }
    
    /**
     * Obtiene una venta específica
     */
    public Venta obtenerVenta(Long ventaId) {
        return ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + ventaId));
    }
    
    /**
     * Sincroniza ventas desde cátedra
     */
    @Transactional
    public void sincronizarVentas() {
        log.info("Sincronizando ventas desde cátedra");
        
        try {
            var ventasDTO = catedraService.listarVentas();
            log.info("Ventas obtenidas desde cátedra: {}", ventasDTO.size());
            
            // Aquí podríamos implementar lógica para reconciliar con BD local
            
        } catch (Exception e) {
            log.error("Error sincronizando ventas: {}", e.getMessage());
        }
    }
    
    /**
     * Reintenta ventas fallidas
     */
    @Transactional
    public void reintentarVentasFallidas() {
        List<Venta> ventasPendientes = ventaRepository.findByEstadoSincronizacion("PENDIENTE");
        
        log.info("Reintentando {} ventas pendientes", ventasPendientes.size());
        
        for (Venta venta : ventasPendientes) {
            if (venta.getIntentosSincronizacion() < 3) {
                try {
                    // Lógica de reintento
                    venta.setIntentosSincronizacion(venta.getIntentosSincronizacion() + 1);
                    venta.setUltimoIntento(LocalDateTime.now());
                    ventaRepository.save(venta);
                    
                } catch (Exception e) {
                    log.error("Error reintentando venta {}: {}", venta.getId(), e.getMessage());
                }
            }
        }
    }
}

