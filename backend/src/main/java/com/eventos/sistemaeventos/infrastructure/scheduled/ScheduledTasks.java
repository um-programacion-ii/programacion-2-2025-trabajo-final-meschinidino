package com.eventos.sistemaeventos.infrastructure.scheduled;

import com.eventos.sistemaeventos.application.service.SesionService;
import com.eventos.sistemaeventos.application.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTasks {
    
    private final SesionService sesionService;
    private final VentaService ventaService;
    
    @Scheduled(fixedRate = 600000)
    public void limpiarSesionesExpiradas() {
        log.debug("Ejecutando limpieza de sesiones expiradas");
        try {
            sesionService.limpiarSesionesExpiradas();
        } catch (Exception e) {
            log.error("Error limpiando sesiones expiradas", e);
        }
    }

    @Scheduled(fixedRate = 300000)
    public void reintentarVentasPendientes() {
        log.debug("Reintentando ventas pendientes");
        try {
            ventaService.reintentarVentasPendientes();
        } catch (Exception e) {
            log.error("Error reintentando ventas pendientes", e);
        }
    }
}
