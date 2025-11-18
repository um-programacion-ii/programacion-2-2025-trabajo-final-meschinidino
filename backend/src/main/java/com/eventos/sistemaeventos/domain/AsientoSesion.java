package com.eventos.sistemaeventos.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Asientos seleccionados durante una sesión
 */
@Entity
@Table(name = "asientos_sesion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoSesion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer fila;
    
    @Column(nullable = false)
    private Integer columna;
    
    private String persona; // Nombre de la persona asignada al asiento
    
    @Column(name = "bloqueado_en_catedra")
    private Boolean bloqueadoEnCatedra = false;
}

