package com.eventos.sistemaeventos.presentation.controller;

import com.eventos.sistemaeventos.application.service.EventoService;
import com.eventos.sistemaeventos.application.service.UsuarioService;
import com.eventos.sistemaeventos.infrastructure.config.SecurityConfig;
import com.eventos.sistemaeventos.infrastructure.security.ServiceJwtProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SyncController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "sync.webhook.token=test-token",
        "service.jwt.secret=test-secret",
        "service.jwt.expected-issuer=eventos-proxy"
})
class SyncControllerTest {

    private static final String AUTH_TOKEN = JWT.create()
            .withIssuer("eventos-proxy")
            .withSubject("service")
            .withIssuedAt(Date.from(Instant.now()))
            .withExpiresAt(Date.from(Instant.now().plusSeconds(300)))
            .sign(Algorithm.HMAC256("test-secret"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventoService eventoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @TestConfiguration
    static class TestBeans {
        @Bean
        EventoService eventoService() {
            return mock(EventoService.class);
        }

        @Bean
        UsuarioService usuarioService() {
            return mock(UsuarioService.class);
        }

        @Bean
        PasswordEncoder passwordEncoder() {
            return mock(PasswordEncoder.class);
        }

        @Bean
        ServiceJwtProvider serviceJwtProvider() {
            return new ServiceJwtProvider(
                    "test-secret",
                    "eventos-backend",
                    "eventos-proxy",
                    300
            );
        }
    }

    @Test
    void webhook_withoutToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/sync/webhook")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoCambio\":\"UPDATE\",\"evento\":{\"id\":1}}"))
            .andExpect(status().isUnauthorized());

        verifyNoInteractions(eventoService);
    }

    @Test
    void webhook_missingTipoCambio_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/sync/webhook")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"evento\":{\"id\":1}}"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoService);
    }

    @Test
    void webhook_missingEvento_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/sync/webhook")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoCambio\":\"UPDATE\"}"))
            .andExpect(status().isBadRequest());

        verifyNoInteractions(eventoService);
    }

    @Test
    void webhook_validPayload_invokesService() throws Exception {
        mockMvc.perform(post("/api/sync/webhook")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"tipoCambio\":\"UPDATE\",\"evento\":{\"id\":1}}"))
            .andExpect(status().isOk());

        verify(eventoService).procesarCambioEvento(eq("UPDATE"), any());
    }
}
