package com.eventos.sistemaeventos.application.service;

import com.eventos.sistemaeventos.application.port.repository.UsuarioRepositoryPort;
import com.eventos.sistemaeventos.domain.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {
    
    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(new ArrayList<>())
                .build();
    }
    
    @Transactional
    public Usuario registrarUsuario(String username, String password, String email, 
                                     String firstName, String lastName) {
        
        if (usuarioRepository.existsByUsername(username)) {
            throw new RuntimeException("El username ya existe");
        }
        
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya existe");
        }
        
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setEmail(email);
        usuario.setFirstName(firstName);
        usuario.setLastName(lastName);
        usuario.setCreatedDate(LocalDateTime.now());
        
        usuario = usuarioRepository.save(usuario);
        log.info("Usuario registrado: {}", username);
        
        return usuario;
    }
    
    public Usuario obtenerUsuario(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    @Transactional
    public void actualizarUltimoLogin(String username) {
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            usuario.setLastLogin(LocalDateTime.now());
            usuarioRepository.save(usuario);
        });
    }
}
