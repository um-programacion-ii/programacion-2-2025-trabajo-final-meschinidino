package com.eventos.sistemaeventos.application.service;

import com.eventos.sistemaeventos.application.dto.catedra.VentaCatedraRequestDTO;
import com.eventos.sistemaeventos.application.dto.catedra.VentaCatedraResponseDTO;
import com.eventos.sistemaeventos.application.port.external.ProxyGateway;
import com.eventos.sistemaeventos.application.port.repository.VentaRepositoryPort;
import com.eventos.sistemaeventos.domain.AsientoVenta;
import com.eventos.sistemaeventos.domain.Sesion;
import com.eventos.sistemaeventos.domain.Venta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VentaService {
    
    private final VentaRepositoryPort ventaRepository;
    private final SesionService sesionService;
    private final EventoService eventoService;
    private final ProxyGateway proxyGateway;

    @Transactional
    public Venta realizarVenta(String username) {
        log.info("Procesando venta para usuario {}", username);
        
        Sesion sesion = sesionService.obtenerSesion(username);
        
        if (sesion.getEventoId() == null || sesion.getAsientosSeleccionados().isEmpty()) {
            throw new RuntimeException("No hay asientos seleccionados para vender");
        }
        
        var evento = eventoService.obtenerEvento(sesion.getEventoId());
        
        VentaCatedraRequestDTO request = new VentaCatedraRequestDTO();
        request.setEventoId(sesion.getEventoId());
        request.setFecha(OffsetDateTime.now(ZoneOffset.UTC));
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
        
        VentaCatedraResponseDTO response = proxyGateway.realizarVenta(request);

        Venta venta = new Venta();
        venta.setEventoId(request.getEventoId());
        venta.setVentaIdCatedra(response.getVentaId());
        if (response.getFechaVenta() != null) {
            venta.setFechaVenta(response.getFechaVenta().toLocalDateTime());
        }
        venta.setPrecioVenta(response.getPrecioVenta() != null ? response.getPrecioVenta() : request.getPrecioVenta());
        venta.setResultado(Boolean.TRUE.equals(response.getResultado()));
        venta.setDescripcion(response.getDescripcion());
        venta.setUsername(username);
        venta.setEstadoSincronizacion(venta.getResultado() ? "CONFIRMADA" : "PENDIENTE");
        
        List<AsientoVenta> asientos = sesion.getAsientosSeleccionados().stream()
                .map(a -> {
                    AsientoVenta asiento = new AsientoVenta();
                    asiento.setFila(a.getFila());
                    asiento.setColumna(a.getColumna());
                    asiento.setPersona(a.getPersona());
                    asiento.setEstado("PENDIENTE");
                    return asiento;
                })
                .collect(Collectors.toList());

        if (response.getAsientos() != null) {
            var estadoPorAsiento = response.getAsientos().stream()
                    .collect(Collectors.toMap(
                            a -> a.getFila() + ":" + a.getColumna(),
                            a -> a
                    ));

            asientos.forEach(asiento -> {
                var key = asiento.getFila() + ":" + asiento.getColumna();
                var estado = estadoPorAsiento.get(key);
                if (estado != null) {
                    asiento.setEstado(estado.getEstado());
                    if (estado.getPersona() != null && !estado.getPersona().isBlank()) {
                        asiento.setPersona(estado.getPersona());
                    }
                }
            });
        }

        venta.getAsientos().addAll(asientos);
        
        venta = ventaRepository.save(venta);
        
        if (venta.getResultado()) {
            sesionService.eliminarSesion(username);
        }
        
        log.info("Venta completada. ID local: {}, ID cátedra: {}, Resultado: {}", 
                venta.getId(), venta.getVentaIdCatedra(), venta.getResultado());
        
        return venta;
    }
    
    public List<Venta> listarVentasUsuario(String username) {
        return ventaRepository.findByUsernameOrderByFechaVentaDesc(username);
    }
    
    public Venta obtenerVenta(Long ventaId) {
        return ventaRepository.findById(ventaId)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada: " + ventaId));
    }
    
    @Transactional
    public void sincronizarVentas() {
        log.info("Sincronizando ventas desde catedra a traves del PROXY");

        try {
            var ventasDTO = proxyGateway.listarVentas();
            log.info("Ventas obtenidas desde catedra: {}", ventasDTO.size());

        } catch (Exception e) {
            log.error("Error sincronizando ventas: {}", e.getMessage());
        }
    }
    
    @Transactional
    public void reintentarVentasPendientes() {
        List<Venta> ventasPendientes = ventaRepository.findByEstadoSincronizacion("PENDIENTE");

        log.info("Reintentando {} ventas pendientes", ventasPendientes.size());

        for (Venta venta : ventasPendientes) {
            try {
                VentaCatedraRequestDTO request = construirRequestDesdeVenta(venta);
                VentaCatedraResponseDTO response = proxyGateway.realizarVenta(request);

                venta.setIntentosSincronizacion(venta.getIntentosSincronizacion() + 1);
                venta.setUltimoIntento(LocalDateTime.now());
                venta.setResultado(Boolean.TRUE.equals(response.getResultado()));
                venta.setDescripcion(response.getDescripcion());
                if (response.getVentaId() != null) {
                    venta.setVentaIdCatedra(response.getVentaId());
                }
                if (response.getFechaVenta() != null) {
                    venta.setFechaVenta(response.getFechaVenta().toLocalDateTime());
                }
                if (response.getPrecioVenta() != null) {
                    venta.setPrecioVenta(response.getPrecioVenta());
                }

                if (response.getAsientos() != null) {
                    var estadoPorAsiento = response.getAsientos().stream()
                            .collect(Collectors.toMap(
                                    a -> a.getFila() + ":" + a.getColumna(),
                                    a -> a
                            ));

                    venta.getAsientos().forEach(asiento -> {
                        var key = asiento.getFila() + ":" + asiento.getColumna();
                        var estado = estadoPorAsiento.get(key);
                        if (estado != null) {
                            asiento.setEstado(estado.getEstado());
                            if (estado.getPersona() != null && !estado.getPersona().isBlank()) {
                                asiento.setPersona(estado.getPersona());
                            }
                        }
                    });
                }

                venta.setEstadoSincronizacion(venta.getResultado() ? "CONFIRMADA" : "PENDIENTE");
                ventaRepository.save(venta);
            } catch (Exception e) {
                venta.setIntentosSincronizacion(venta.getIntentosSincronizacion() + 1);
                venta.setUltimoIntento(LocalDateTime.now());
                ventaRepository.save(venta);
                log.error("Error reintentando venta {}: {}", venta.getId(), e.getMessage());
            }
        }
    }

    private VentaCatedraRequestDTO construirRequestDesdeVenta(Venta venta) {
        VentaCatedraRequestDTO request = new VentaCatedraRequestDTO();
        request.setEventoId(venta.getEventoId());
        request.setFecha(OffsetDateTime.now(ZoneOffset.UTC));
        request.setPrecioVenta(venta.getPrecioVenta());
        request.setAsientos(
                venta.getAsientos().stream()
                        .map(a -> new VentaCatedraRequestDTO.AsientoVentaDTO(
                                a.getFila(),
                                a.getColumna(),
                                a.getPersona()
                        ))
                        .collect(Collectors.toList())
        );
        return request;
    }
}
