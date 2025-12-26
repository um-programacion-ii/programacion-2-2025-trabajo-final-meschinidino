package com.eventos.sistemaeventos.application.port.repository;

import com.eventos.sistemaeventos.domain.Sesion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SesionRepositoryPort {

    Optional<Sesion> findByUsername(String username);

    Sesion save(Sesion sesion);

    void delete(Sesion sesion);

    List<Sesion> findByLastActivityBefore(LocalDateTime expirationThreshold);

    void deleteByLastActivityBefore(LocalDateTime expirationThreshold);
}
