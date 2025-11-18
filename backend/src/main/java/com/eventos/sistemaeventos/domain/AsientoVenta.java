package com.eventos.sistemaeventos.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Asientos vendidos en una venta
 */
@Entity
@Table(name = "asientos_venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsientoVenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer fila;
    
    @Column(nullable = false)
    private Integer columna;
    
    @Column(nullable = false)
    private String persona;
    
    /**
     * Estado del asiento:
     * - "Vendido": Venta exitosa
     * - "Libre": Venta fallida
     * - "Ocupado": Ya estaba ocupado
     * - "Bloqueado": Estaba bloqueado por otro
     */
    private String estado;
}

