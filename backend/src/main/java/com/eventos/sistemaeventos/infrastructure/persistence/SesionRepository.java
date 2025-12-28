package com.eventos.sistemaeventos.infrastructure.persistence;

import com.eventos.sistemaeventos.application.port.repository.SesionRepositoryPort;
import com.eventos.sistemaeventos.domain.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionRepository extends JpaRepository<Sesion, String>, SesionRepositoryPort {
    
    Optional<Sesion> findByUsername(String username);
    
    List<Sesion> findByLastActivityBefore(LocalDateTime dateTime);
    
    void deleteByLastActivityBefore(LocalDateTime dateTime);
}
