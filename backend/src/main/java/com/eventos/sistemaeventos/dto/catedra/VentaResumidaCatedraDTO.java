package com.eventos.sistemaeventos.dto.catedra;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para venta resumida (Payload 8)
 */
@Data
public class VentaResumidaCatedraDTO {
    private Long eventoId;
    private Long ventaId;
    private LocalDateTime fechaVenta;
    private Boolean resultado;
    private String descripcion;
    private BigDecimal precioVenta;
    private Integer cantidadAsientos;
}

