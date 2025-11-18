package com.eventos.sistemaeventos.dto.catedra;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request para realizar venta (Payload 7)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaCatedraRequestDTO {
    private Long eventoId;
    private LocalDateTime fecha;
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

