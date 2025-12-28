package com.eventos.sistemaeventos.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "eventos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 500)
    private String resumen;

    @Column(length = 2000)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha;

    private String direccion;

    @Column(length = 1000)
    private String imagen;

    @Column(name = "fila_asientos")
    private Integer filaAsientos;

    @Column(name = "columna_asientos")
    private Integer columnaAsientos;

    @Column(name = "precio_entrada", precision = 10, scale = 2)
    private BigDecimal precioEntrada;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "nombre", column = @Column(name = "evento_tipo_nombre")),
        @AttributeOverride(name = "descripcion", column = @Column(name = "evento_tipo_descripcion"))
    })
    private EventoTipo eventoTipo;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id")
    private List<Integrante> integrantes = new ArrayList<>();

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "ultima_sincronizacion")
    private LocalDateTime ultimaSincronizacion;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        ultimaSincronizacion = LocalDateTime.now();
    }

    public Integer getTotalAsientos() {
        if (filaAsientos != null && columnaAsientos != null) {
            return filaAsientos * columnaAsientos;
        }
        return 0;
    }
}
