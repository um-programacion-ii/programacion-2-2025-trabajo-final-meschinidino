package com.eventos.sistemaeventos.service;

import com.eventos.sistemaeventos.domain.AsientoSesion;
import com.eventos.sistemaeventos.domain.Sesion;
import com.eventos.sistemaeventos.dto.catedra.BloquearAsientosRequestDTO;
import com.eventos.sistemaeventos.dto.catedra.BloquearAsientosResponseDTO;
import com.eventos.sistemaeventos.repository.SesionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar sesiones de usuario
 * Referencia: Sección 4.4 del documento
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SesionService {
    
    private final SesionRepository sesionRepository;
    private final CatedraService catedraService;
    
    /**
     * Crea o recupera una sesión para un usuario
     */
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
    
    /**
     * Crea una nueva sesión
     */
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
    
    /**
     * Actualiza el paso actual de la sesión
     */
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
    
    /**
     * Agrega asientos a la sesión
     */
    @Transactional
    public Sesion agregarAsientos(String username, List<AsientoSesion> asientos) {
        Sesion sesion = obtenerOCrearSesion(username);
        
        // Limpiar asientos previos
        sesion.getAsientosSeleccionados().clear();
        sesion.getAsientosSeleccionados().addAll(asientos);
        
        sesion.setPaso("SELECCION_ASIENTOS");
        sesion.updateActivity();
        
        log.info("Asientos agregados a sesión: {} asientos para usuario {}", 
                asientos.size(), username);
        
        return sesionRepository.save(sesion);
    }
    
    /**
     * Bloquea los asientos seleccionados en cátedra
     */
    @Transactional
    public BloquearAsientosResponseDTO bloquearAsientos(String username) {
        Sesion sesion = obtenerOCrearSesion(username);
        
        if (sesion.getEventoId() == null || sesion.getAsientosSeleccionados().isEmpty()) {
            throw new RuntimeException("No hay asientos seleccionados para bloquear");
        }
        
        // Preparar request para cátedra
        BloquearAsientosRequestDTO request = new BloquearAsientosRequestDTO();
        request.setEventoId(sesion.getEventoId());
        request.setAsientos(
            sesion.getAsientosSeleccionados().stream()
                .map(a -> new BloquearAsientosRequestDTO.AsientoDTO(a.getFila(), a.getColumna()))
                .collect(Collectors.toList())
        );
        
        // Bloquear en cátedra
        BloquearAsientosResponseDTO response = catedraService.bloquearAsientos(request);
        
        // Marcar asientos como bloqueados
        if (response.getResultado()) {
            sesion.getAsientosSeleccionados().forEach(a -> a.setBloqueadoEnCatedra(true));
            sesion.setPaso("CARGA_DATOS");
            sesionRepository.save(sesion);
        }
        
        return response;
    }
    
    /**
     * Obtiene la sesión actual de un usuario
     */
    public Sesion obtenerSesion(String username) {
        return sesionRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("No existe sesión para el usuario"));
    }
    
    /**
     * Limpia sesiones expiradas (para tarea programada)
     */
    @Transactional
    public void limpiarSesionesExpiradas() {
        LocalDateTime expiracion = LocalDateTime.now().minusMinutes(30);
        List<Sesion> sesionesExpiradas = sesionRepository.findByLastActivityBefore(expiracion);
        
        log.info("Limpiando {} sesiones expiradas", sesionesExpiradas.size());
        sesionRepository.deleteByLastActivityBefore(expiracion);
    }
    
    /**
     * Elimina la sesión de un usuario
     */
    @Transactional
    public void eliminarSesion(String username) {
        sesionRepository.findByUsername(username)
                .ifPresent(sesion -> {
                    log.info("Eliminando sesión de usuario {}", username);
                    sesionRepository.delete(sesion);
                });
    }
}

