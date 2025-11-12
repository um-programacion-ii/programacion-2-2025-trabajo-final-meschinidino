package com.eventos.proxy.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Servicio para consultar Redis de cátedra
 * Solo lectura del estado compartido
 */
@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.application.name:eventos-proxy}")
    private String serviceName;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Obtiene el estado de los asientos de un evento desde Redis
     * La clave se construye como: "evento:{eventoId}:asientos"
     */
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

    /**
     * Obtiene un valor específico de Redis
     */
    public Object obtener(String key) {
        log.debug("Obteniendo clave: {}", key);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Verifica si existe una clave en Redis
     */
    public boolean existe(String key) {
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }
}

