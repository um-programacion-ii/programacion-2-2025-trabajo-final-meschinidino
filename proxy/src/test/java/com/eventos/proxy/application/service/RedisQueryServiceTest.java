package com.eventos.proxy.application.service;

import com.eventos.proxy.application.port.out.RedisSeatStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisQueryServiceTest {

    @Mock
    private RedisSeatStore redisSeatStore;

    @InjectMocks
    private RedisQueryService redisQueryService;

    @Test
    void obtenerEstadoAsientos_retornaMapa() {
        Map<Object, Object> expected = Map.of("fila:1:columna:1", "Libre");
        when(redisSeatStore.obtenerEstadoAsientos(5L)).thenReturn(expected);

        Map<Object, Object> result = redisQueryService.obtenerEstadoAsientos(5L);

        assertThat(result).isSameAs(expected);
        verify(redisSeatStore).obtenerEstadoAsientos(5L);
    }

    @Test
    void obtenerEstadoAsiento_retornaDesconocidoSiNoExiste() {
        when(redisSeatStore.obtenerEstadoAsiento(2L, 1, 3)).thenReturn(null);

        String result = redisQueryService.obtenerEstadoAsiento(2L, 1, 3);

        assertThat(result).isEqualTo("Desconocido");
        verify(redisSeatStore).obtenerEstadoAsiento(2L, 1, 3);
    }

    @Test
    void obtenerEstadoAsiento_retornaValorSiExiste() {
        when(redisSeatStore.obtenerEstadoAsiento(3L, 2, 4)).thenReturn("Ocupado");

        String result = redisQueryService.obtenerEstadoAsiento(3L, 2, 4);

        assertThat(result).isEqualTo("Ocupado");
        verify(redisSeatStore).obtenerEstadoAsiento(3L, 2, 4);
    }
}
