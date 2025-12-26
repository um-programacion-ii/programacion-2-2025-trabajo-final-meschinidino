package com.eventos.proxy.infrastructure.web;

import com.eventos.proxy.application.port.in.CatedraProxyUseCase;
import com.eventos.proxy.application.port.in.RedisQueryUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProxyController.class)
class ProxyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RedisQueryUseCase redisQueryUseCase;

    @MockitoBean
    private CatedraProxyUseCase catedraProxyUseCase;

    @Test
    void obtenerAsientos_retornaMapa() throws Exception {
        Map<Object, Object> response = Map.of("fila:1:columna:1", "Libre");
        when(redisQueryUseCase.obtenerEstadoAsientos(1L)).thenReturn(response);

        mockMvc.perform(get("/api/proxy/asientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['fila:1:columna:1']").value("Libre"));
    }

    @Test
    void obtenerAsientos_retornaErrorSiFallaRedis() throws Exception {
        when(redisQueryUseCase.obtenerEstadoAsientos(9L)).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/proxy/asientos/9"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error consultando estado de asientos"));
    }

    @Test
    void obtenerAsiento_retornaDetalle() throws Exception {
        when(redisQueryUseCase.obtenerEstadoAsiento(2L, 3, 4)).thenReturn("Ocupado");

        mockMvc.perform(get("/api/proxy/asientos/2/3/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventoId").value(2))
                .andExpect(jsonPath("$.fila").value(3))
                .andExpect(jsonPath("$.columna").value(4))
                .andExpect(jsonPath("$.estado").value("Ocupado"));
    }

    @Test
    void obtenerEventosResumidos_retornaLista() throws Exception {
        List<Map<String, Object>> response = List.of(Map.of("id", 10));
        when(catedraProxyUseCase.obtenerEventosResumidos()).thenReturn(response);

        mockMvc.perform(get("/api/proxy/catedra/eventos-resumidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void obtenerEventosCompletos_retornaLista() throws Exception {
        List<Map<String, Object>> response = List.of(Map.of("id", 11));
        when(catedraProxyUseCase.obtenerEventosCompletos()).thenReturn(response);

        mockMvc.perform(get("/api/proxy/catedra/eventos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(11));
    }

    @Test
    void obtenerEvento_retornaMapa() throws Exception {
        Map<String, Object> response = Map.of("id", 12);
        when(catedraProxyUseCase.obtenerEvento(12L)).thenReturn(response);

        mockMvc.perform(get("/api/proxy/catedra/evento/12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(12));
    }

    @Test
    void bloquearAsientos_retornaRespuesta() throws Exception {
        Map<String, Object> request = Map.of("eventoId", 15);
        Map<String, Object> response = Map.of("ok", true);
        when(catedraProxyUseCase.bloquearAsientos(request)).thenReturn(response);

        mockMvc.perform(post("/api/proxy/catedra/bloquear-asientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    void realizarVenta_retornaRespuesta() throws Exception {
        Map<String, Object> request = Map.of("eventoId", 20);
        Map<String, Object> response = Map.of("ventaId", 99);
        when(catedraProxyUseCase.realizarVenta(request)).thenReturn(response);

        mockMvc.perform(post("/api/proxy/catedra/realizar-venta")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventaId").value(99));
    }

    @Test
    void listarVentas_retornaLista() throws Exception {
        List<Map<String, Object>> response = List.of(Map.of("ventaId", 1));
        when(catedraProxyUseCase.listarVentas()).thenReturn(response);

        mockMvc.perform(get("/api/proxy/catedra/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ventaId").value(1));
    }

    @Test
    void obtenerVenta_retornaMapa() throws Exception {
        Map<String, Object> response = Map.of("ventaId", 5);
        when(catedraProxyUseCase.obtenerVenta(5L)).thenReturn(response);

        mockMvc.perform(get("/api/proxy/catedra/venta/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ventaId").value(5));
    }
}
