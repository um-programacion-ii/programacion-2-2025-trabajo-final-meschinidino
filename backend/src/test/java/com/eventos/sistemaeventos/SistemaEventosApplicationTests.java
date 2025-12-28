package com.eventos.sistemaeventos;

import com.eventos.sistemaeventos.application.port.external.ProxyGateway;
import com.eventos.sistemaeventos.infrastructure.persistence.EventoRepository;
import com.eventos.sistemaeventos.infrastructure.persistence.UsuarioRepository;
import com.eventos.sistemaeventos.infrastructure.persistence.VentaRepository;
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
    private ProxyGateway proxyGateway;

    @Test
    void contextLoads() {
    }

    @Test
    void repositoriesAreInjected() {
        assertThat(usuarioRepository).isNotNull();
        assertThat(eventoRepository).isNotNull();
        assertThat(ventaRepository).isNotNull();
    }

    @Test
    void servicesAreInjected() {
        assertThat(proxyGateway).isNotNull();
    }
}
