package com.eventos.sistemaeventos.application.service;

import com.eventos.sistemaeventos.application.dto.catedra.BloquearAsientosRequestDTO;
import com.eventos.sistemaeventos.application.dto.catedra.BloquearAsientosResponseDTO;
import com.eventos.sistemaeventos.application.port.external.ProxyGateway;
import com.eventos.sistemaeventos.application.port.repository.SesionRepositoryPort;
import com.eventos.sistemaeventos.domain.AsientoSesion;
import com.eventos.sistemaeventos.domain.Sesion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SesionService {
    
    private final SesionRepositoryPort sesionRepository;
    private final ProxyGateway proxyGateway;

    @Transactional
    public Sesion obtenerOCrearSesion(String username) {
        return sesionRepository.findByUsername(username)
                .map(sesion -> {
                    if (sesion.isExpired()) {
                        log.info("Sesión expirada para usuario {}, creando nueva", username);
                        sesionRepository.delete(sesion);
                        return crearNuevaSesion(username);
                    }
                    sesion.updateActivity();
                    return sesionRepository.save(sesion);
                })
                .orElseGet(() -> crearNuevaSesion(username));
    }
    
    private Sesion crearNuevaSesion(String username) {
        Sesion sesion = new Sesion();
        sesion.setSessionId(UUID.randomUUID().toString());
        sesion.setUsername(username);
        sesion.setPaso("LISTADO_EVENTOS");
        sesion.setCreatedAt(LocalDateTime.now());
        sesion.updateActivity();
        
        log.info("Nueva sesión creada para usuario {}: {}", username, sesion.getSessionId());
        return sesionRepository.save(sesion);
    }
    
    @Transactional
    public Sesion actualizarPaso(String username, String paso, Long eventoId) {
        Sesion sesion = obtenerOCrearSesion(username);
        sesion.setPaso(paso);
        
        if (eventoId != null) {
            sesion.setEventoId(eventoId);
        }
        
        sesion.updateActivity();
        return sesionRepository.save(sesion);
    }
    
    @Transactional
    public Sesion agregarAsientos(String username, List<AsientoSesion> asientos) {
        Sesion sesion = obtenerOCrearSesion(username);
        
        sesion.getAsientosSeleccionados().clear();
        sesion.getAsientosSeleccionados().addAll(asientos);
        
        sesion.setPaso("SELECCION_ASIENTOS");
        sesion.updateActivity();
        
        log.info("Asientos agregados a sesión: {} asientos para usuario {}", 
                asientos.size(), username);
        
        return sesionRepository.save(sesion);
    }
    
    @Transactional
    public BloquearAsientosResponseDTO bloquearAsientos(String username) {
        Sesion sesion = obtenerOCrearSesion(username);
        
        if (sesion.getEventoId() == null) {
            throw new RuntimeException("No hay evento seleccionado para bloquear asientos");
        }

        if (sesion.getAsientosSeleccionados().isEmpty()) {
            throw new RuntimeException("No hay asientos seleccionados para bloquear");
        }
        
        log.info("Bloqueando {} asientos para evento {} del usuario {}",
                sesion.getAsientosSeleccionados().size(), sesion.getEventoId(), username);

        BloquearAsientosRequestDTO request = new BloquearAsientosRequestDTO();
        request.setEventoId(sesion.getEventoId());
        request.setAsientos(
            sesion.getAsientosSeleccionados().stream()
                .map(a -> new BloquearAsientosRequestDTO.AsientoDTO(a.getFila(), a.getColumna()))
                .collect(Collectors.toList())
        );
        
        BloquearAsientosResponseDTO response = proxyGateway.bloquearAsientos(request);

        if (response.getResultado()) {
            sesion.getAsientosSeleccionados().forEach(a -> a.setBloqueadoEnCatedra(true));
            sesion.setPaso("CARGA_DATOS");
            sesionRepository.save(sesion);
        }
        
        return response;
    }
    
    public Sesion obtenerSesion(String username) {
        return sesionRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No existe sesión para el usuario"));
    }
    
    @Transactional
    public void limpiarSesionesExpiradas() {
        LocalDateTime expiracion = LocalDateTime.now().minusMinutes(30);
        List<Sesion> sesionesExpiradas = sesionRepository.findByLastActivityBefore(expiracion);
        
        log.info("Limpiando {} sesiones expiradas", sesionesExpiradas.size());
        sesionRepository.deleteByLastActivityBefore(expiracion);
    }
    
    @Transactional
    public void eliminarSesion(String username) {
        sesionRepository.findByUsername(username)
                .ifPresent(sesion -> {
                    log.info("Eliminando sesión de usuario {}", username);
                    sesionRepository.delete(sesion);
                });
    }
}
