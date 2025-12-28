package com.eventos.sistemaeventos.application.dto.catedra;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaCatedraRequestDTO {
    private Long eventoId;
    private OffsetDateTime fecha;
    private BigDecimal precioVenta;
    private List<AsientoVentaDTO> asientos;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AsientoVentaDTO {
        private Integer fila;
        private Integer columna;
        private String persona;
    }
}
