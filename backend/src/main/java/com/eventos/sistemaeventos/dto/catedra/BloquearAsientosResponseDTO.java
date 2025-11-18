package com.eventos.sistemaeventos.dto.catedra;

import lombok.Data;
import java.util.List;

/**
 * Response de bloquear asientos (Payload 6)
 */
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
        private String estado; // "Bloqueo exitoso", "Ocupado", "Bloqueado"
    }
}

