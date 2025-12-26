package com.eventos.proxy.infrastructure.redis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisSeatRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Test
    void obtenerEstadoAsientos_retornaMapa() {
        RedisSeatRepository repository = new RedisSeatRepository(redisTemplate);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        String key = "evento:5:asientos";
        Map<Object, Object> expected = Map.of("fila:1:columna:1", "Libre");
        when(hashOperations.entries(key)).thenReturn(expected);

        Map<Object, Object> result = repository.obtenerEstadoAsientos(5L);

        assertThat(result).isSameAs(expected);
    }

    @Test
    void obtenerEstadoAsiento_retornaValor() {
        RedisSeatRepository repository = new RedisSeatRepository(redisTemplate);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        String key = "evento:7:asientos";
        String hashKey = "fila:2:columna:3";
        when(hashOperations.get(key, hashKey)).thenReturn("Ocupado");

        Object result = repository.obtenerEstadoAsiento(7L, 2, 3);

        assertThat(result).isEqualTo("Ocupado");
    }
}
