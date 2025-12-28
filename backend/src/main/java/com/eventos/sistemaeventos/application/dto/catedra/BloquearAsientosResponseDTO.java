package com.eventos.sistemaeventos.application.dto.catedra;

import lombok.Data;
import java.util.List;

@Data
public class BloquearAsientosResponseDTO {
    private Boolean resultado;
    private String descripcion;
    private Long eventoId;
    private List<AsientoEstadoDTO> asientos;
    
    @Data
    public static class AsientoEstadoDTO {
        private Integer fila;
        private Integer columna;
        private String estado;
    }
}
