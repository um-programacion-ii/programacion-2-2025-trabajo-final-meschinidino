package com.eventos.sistemaeventos.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeleccionAsientosRequestDTO {
    private Long eventoId;
    private List<AsientoDTO> asientos;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AsientoDTO {
        private Integer fila;
        private Integer columna;
        private String persona;
    }
}

