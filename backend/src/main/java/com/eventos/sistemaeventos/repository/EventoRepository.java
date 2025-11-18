package com.eventos.sistemaeventos.repository;

import com.eventos.sistemaeventos.domain.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    List<Evento> findByActivoTrue();
    
    List<Evento> findByFechaAfter(LocalDateTime fecha);
    
    List<Evento> findByActivoTrueAndFechaAfter(LocalDateTime fecha);
}

