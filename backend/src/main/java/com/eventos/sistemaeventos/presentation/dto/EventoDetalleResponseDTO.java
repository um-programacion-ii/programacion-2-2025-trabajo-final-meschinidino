package com.eventos.sistemaeventos.presentation.dto;

import com.eventos.sistemaeventos.domain.Evento;
import lombok.Data;

@Data
public class EventoDetalleResponseDTO {
    private Evento evento;
    private Object asientos;
}
