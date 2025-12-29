package com.eventos.sistemaeventos.application.dto.catedra;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class VentaCatedraResponseDTO {
    private Long eventoId;
    private Long ventaId;
    private OffsetDateTime fechaVenta;
    private Boolean resultado;
    private String descripcion;
    private BigDecimal precioVenta;
    private List<AsientoVentaDTO> asientos;
    
    @Data
    public static class AsientoVentaDTO {
        private Integer fila;
        private Integer columna;
        private String persona;
        private String estado;
    }
}
