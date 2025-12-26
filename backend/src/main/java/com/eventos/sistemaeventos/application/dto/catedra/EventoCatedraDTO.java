package com.eventos.sistemaeventos.application.dto.catedra;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class EventoCatedraDTO {
    private Long id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private OffsetDateTime fecha;
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
