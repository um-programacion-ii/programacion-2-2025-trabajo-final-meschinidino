package com.eventos.sistemaeventos.application.port.repository;

import com.eventos.sistemaeventos.domain.Evento;

import java.util.List;
import java.util.Optional;

public interface EventoRepositoryPort {

    List<Evento> findByActivoTrue();

    Optional<Evento> findById(Long id);

    Evento save(Evento evento);
}
