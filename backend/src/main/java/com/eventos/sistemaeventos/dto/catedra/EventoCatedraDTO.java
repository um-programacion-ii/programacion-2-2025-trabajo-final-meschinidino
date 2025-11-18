package com.eventos.sistemaeventos.dto.catedra;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para evento completo (Payload 4 y 5)
 */
@Data
public class EventoCatedraDTO {
    private Long id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private LocalDateTime fecha;
    private String direccion;
    private String imagen;
    private Integer filaAsientos;
    private Integer columnaAsientos;
    private BigDecimal precioEntrada;
    private EventoTipoDTO eventoTipo;
    private List<IntegranteDTO> integrantes;
    
    @Data
    public static class EventoTipoDTO {
        private String nombre;
        private String descripcion;
    }
    
    @Data
    public static class IntegranteDTO {
        private String nombre;
        private String apellido;
        private String identificacion;
    }
}

