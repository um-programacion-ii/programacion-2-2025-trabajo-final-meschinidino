package com.eventos.sistemaeventos.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sesiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sesion {
    
    @Id
    private String sessionId;
    
    @Column(nullable = false)
    private String username;
    
    private Long eventoId;
    
    @Column(nullable = false)
    private String paso;
    
    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "sesion_id")
    private List<AsientoSesion> asientosSeleccionados = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updateActivity();
    }
    
    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return lastActivity.plusMinutes(30).isBefore(LocalDateTime.now());
    }
}
