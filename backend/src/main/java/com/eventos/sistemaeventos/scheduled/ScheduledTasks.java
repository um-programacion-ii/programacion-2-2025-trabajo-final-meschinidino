package com.eventos.sistemaeventos.scheduled;

import com.eventos.sistemaeventos.service.SesionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tareas programadas
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    
    private final SesionService sesionService;
    
    /**
     * Limpia sesiones expiradas cada 10 minutos
     */
    @Scheduled(fixedRate = 600000) // 10 minutos
    public void limpiarSesionesExpiradas() {
        log.debug("Ejecutando limpieza de sesiones expiradas");
        try {
            sesionService.limpiarSesionesExpiradas();
        } catch (Exception e) {
            log.error("Error limpiando sesiones expiradas", e);
        }
    }
}

