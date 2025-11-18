package com.eventos.sistemaeventos.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Tipo de evento (embebido en Evento)
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoTipo {
    
    private String nombre; // "Conferencia", "Obra de teatro", etc.
    private String descripcion;
}

