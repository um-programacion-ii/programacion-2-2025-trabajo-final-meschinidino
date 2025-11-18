package com.eventos.sistemaeventos.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Venta - Registro local de ventas
 * Referencia: Payload 7, 8 y 9 del documento
 */
@Entity
@Table(name = "ventas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "evento_id", nullable = false)
    private Long eventoId;
    
    @Column(name = "venta_id_catedra")
    private Long ventaIdCatedra; // ID de venta en el sistema de cátedra
    
    @Column(name = "fecha_venta", nullable = false)
    private LocalDateTime fechaVenta;
    
    @Column(name = "precio_venta", precision = 10, scale = 2)
    private BigDecimal precioVenta;
    
    @Column(nullable = false)
    private Boolean resultado = false; // true: exitosa, false: fallida
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(nullable = false)
    private String username; // Usuario que realizó la compra
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "venta_id")
    private List<AsientoVenta> asientos = new ArrayList<>();
    
    /**
     * Estado de sincronización con cátedra:
     * - "PENDIENTE": No se ha enviado a cátedra
     * - "PROCESANDO": Se está intentando enviar
     * - "CONFIRMADA": Confirmada por cátedra
     * - "FALLIDA": Rechazada por cátedra
     */
    @Column(name = "estado_sincronizacion")
    private String estadoSincronizacion = "PENDIENTE";
    
    @Column(name = "intentos_sincronizacion")
    private Integer intentosSincronizacion = 0;
    
    @Column(name = "ultimo_intento")
    private LocalDateTime ultimoIntento;
    
    @PrePersist
    protected void onCreate() {
        if (fechaVenta == null) {
            fechaVenta = LocalDateTime.now();
        }
    }
}

