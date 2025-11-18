package com.eventos.sistemaeventos.dto.catedra;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response de venta (Payload 7)
 */
@Data
public class VentaCatedraResponseDTO {
    private Long eventoId;
    private Long ventaId;
    private LocalDateTime fechaVenta;
    private Boolean resultado;
    private String descripcion;
    private BigDecimal precioVenta;
    private List<AsientoVentaDTO> asientos;
    
    @Data
    public static class AsientoVentaDTO {
        private Integer fila;
        private Integer columna;
        private String persona;
        private String estado; // "Vendido", "Libre", "Ocupado"
    }
}

