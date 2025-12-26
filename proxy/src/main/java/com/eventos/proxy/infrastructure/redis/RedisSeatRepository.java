package com.eventos.proxy.infrastructure.redis;

import com.eventos.proxy.application.port.out.RedisSeatStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class RedisSeatRepository implements RedisSeatStore {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisSeatRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Map<Object, Object> obtenerEstadoAsientos(Long eventoId) {
        String key = String.format("evento:%d:asientos", eventoId);
        log.debug("Consultando Redis - Key: {}", key);

        try {
            Map<Object, Object> asientos = redisTemplate.opsForHash().entries(key);
            log.info("Asientos obtenidos para evento {}: {} asientos", eventoId, asientos.size());
            return asientos;
        } catch (Exception e) {
            log.error("Error consultando Redis para evento {}: {}", eventoId, e.getMessage());
            throw new RuntimeException("Error consultando estado de asientos", e);
        }
    }

    @Override
    public Object obtenerEstadoAsiento(Long eventoId, int fila, int columna) {
        String key = String.format("evento:%d:asientos", eventoId);
        String hashKey = String.format("fila:%d:columna:%d", fila, columna);
        log.debug("Consultando asiento - key: {}, hashKey: {}", key, hashKey);
        return redisTemplate.opsForHash().get(key, hashKey);
    }
}
