package com.eventos.sistemaeventos.application.port.repository;

import com.eventos.sistemaeventos.domain.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Usuario save(Usuario usuario);
}
