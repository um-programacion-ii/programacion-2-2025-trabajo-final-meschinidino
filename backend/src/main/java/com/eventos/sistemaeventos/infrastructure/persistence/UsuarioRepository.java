package com.eventos.sistemaeventos.infrastructure.persistence;

import com.eventos.sistemaeventos.application.port.repository.UsuarioRepositoryPort;
import com.eventos.sistemaeventos.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuarioRepositoryPort {
    Optional<Usuario> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
