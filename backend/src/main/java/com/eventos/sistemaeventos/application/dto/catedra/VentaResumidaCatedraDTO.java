package com.eventos.sistemaeventos.application.dto.catedra;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class VentaResumidaCatedraDTO {
    private Long eventoId;
    private Long ventaId;
    private OffsetDateTime fechaVenta;
    private Boolean resultado;
    private String descripcion;
    private BigDecimal precioVenta;
    private Integer cantidadAsientos;
}
