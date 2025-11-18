package com.eventos.sistemaeventos.dto.catedra;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para eventos resumidos (Payload 3)
 */
@Data
public class EventoResumidoCatedraDTO {
    private Long id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private LocalDateTime fecha;
    private BigDecimal precioEntrada;
    private EventoTipoDTO eventoTipo;
    
    @Data
    public static class EventoTipoDTO {
        private String nombre;
        private String descripcion;
    }
}

