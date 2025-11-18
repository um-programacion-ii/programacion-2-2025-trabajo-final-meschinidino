package com.eventos.sistemaeventos.dto.catedra;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Request para bloquear asientos (Payload 6)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloquearAsientosRequestDTO {
    private Long eventoId;
    private List<AsientoDTO> asientos;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AsientoDTO {
        private Integer fila;
        private Integer columna;
    }
}

