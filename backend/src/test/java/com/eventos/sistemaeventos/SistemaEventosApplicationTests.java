package com.eventos.sistemaeventos;

import com.eventos.sistemaeventos.repository.EventoRepository;
import com.eventos.sistemaeventos.repository.UsuarioRepository;
import com.eventos.sistemaeventos.repository.VentaRepository;
import com.eventos.sistemaeventos.service.CatedraService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SistemaEventosApplicationTests {

    @Autowired(required = false)
    private UsuarioRepository usuarioRepository;

    @Autowired(required = false)
    private EventoRepository eventoRepository;

    @Autowired(required = false)
    private VentaRepository ventaRepository;

    @Autowired(required = false)
    private CatedraService catedraService;

    @Test
    void contextLoads() {
        // Verifica que el contexto de Spring carga correctamente
    }

    @Test
    void repositoriesAreInjected() {
        // Verifica que los repositorios se inyectan correctamente
        assertThat(usuarioRepository).isNotNull();
        assertThat(eventoRepository).isNotNull();
        assertThat(ventaRepository).isNotNull();
    }

    @Test
    void servicesAreInjected() {
        // Verifica que los servicios se inyectan correctamente
        assertThat(catedraService).isNotNull();
    }
}

