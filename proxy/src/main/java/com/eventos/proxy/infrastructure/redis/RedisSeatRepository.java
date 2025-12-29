package com.eventos.proxy.infrastructure.redis;

import com.eventos.proxy.application.port.out.RedisSeatStore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Map;

@Slf4j
@Service
public class RedisSeatRepository implements RedisSeatStore {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisSeatRepository(
            RedisTemplate<String, Object> redisTemplate,
            StringRedisTemplate stringRedisTemplate,
            ObjectMapper objectMapper
    ) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<Object, Object> obtenerEstadoAsientos(Long eventoId) {
        String hashKey = String.format("evento:%d:asientos", eventoId);
        log.debug("Consultando Redis - Key: {}", hashKey);

        try {
            Map<Object, Object> fromCatedraJson = readCatedraJson(eventoId);
            if (!fromCatedraJson.isEmpty()) {
                log.info("Asientos obtenidos para evento {} desde JSON: {} asientos", eventoId, fromCatedraJson.size());
                return fromCatedraJson;
            }

            Map<Object, Object> asientos = redisTemplate.opsForHash().entries(hashKey);
            if (!asientos.isEmpty()) {
                log.info("Asientos obtenidos para evento {} desde hash: {} asientos", eventoId, asientos.size());
                return asientos;
            }

            Map<Object, Object> fromPerSeatKeys = readPerSeatKeys(eventoId);
            if (!fromPerSeatKeys.isEmpty()) {
                log.info("Asientos obtenidos para evento {} desde keys individuales: {} asientos", eventoId, fromPerSeatKeys.size());
                return fromPerSeatKeys;
            }

            log.info("Asientos obtenidos para evento {}: 0 asientos", eventoId);
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("Error consultando Redis para evento {}: {}", eventoId, e.getMessage());
            throw new RuntimeException("Error consultando estado de asientos", e);
        }
    }

    @Override
    public Object obtenerEstadoAsiento(Long eventoId, int fila, int columna) {
        String hashKey = String.format("evento:%d:asientos", eventoId);
        String seatKey = String.format("fila:%d:columna:%d", fila, columna);
        log.debug("Consultando asiento - key: {}, hashKey: {}", hashKey, seatKey);

        Object estado = redisTemplate.opsForHash().get(hashKey, seatKey);
        if (estado != null) {
            return estado;
        }

        Map<Object, Object> fromCatedraJson = readCatedraJson(eventoId);
        if (!fromCatedraJson.isEmpty()) {
            return fromCatedraJson.get(seatKey);
        }

        Object owner = stringRedisTemplate.opsForValue().get(perSeatKey(eventoId, fila, columna));
        return owner != null ? "Bloqueado" : null;
    }

    private Map<Object, Object> readCatedraJson(Long eventoId) {
        String key = "evento_" + eventoId;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode asientos = root.get("asientos");
            if (asientos == null || !asientos.isArray()) {
                return Collections.emptyMap();
            }

            Map<Object, Object> mapped = new LinkedHashMap<>();
            for (JsonNode asiento : asientos) {
                int fila = asiento.path("fila").asInt(-1);
                int columna = asiento.path("columna").asInt(-1);
                String estado = asiento.path("estado").asText(null);
                if (fila <= 0 || columna <= 0 || estado == null || estado.isBlank()) {
                    continue;
                }
                String seatKey = String.format("fila:%d:columna:%d", fila, columna);
                mapped.put(seatKey, estado);
            }
            return mapped;
        } catch (Exception e) {
            log.debug("Error parseando JSON de Redis para evento {}: {}", eventoId, e.toString());
            return Collections.emptyMap();
        }
    }

    private Map<Object, Object> readPerSeatKeys(Long eventoId) {
        String pattern = String.format("evento:%d:asiento:*", eventoId);
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Object, Object> mapped = new LinkedHashMap<>();
        for (String key : keys) {
            SeatPosition pos = parsePerSeatKey(key);
            if (pos == null) {
                continue;
            }
            if (pos.eventoId() != eventoId) {
                continue;
            }
            String seatKey = String.format("fila:%d:columna:%d", pos.fila(), pos.columna());
            // El valor guarda quién bloqueó el asiento; si existe, lo marcamos como bloqueado.
            mapped.put(seatKey, "Bloqueado");
        }
        return mapped;
    }

    private String perSeatKey(Long eventoId, int fila, int columna) {
        return String.format("evento:%d:asiento:%d-%d", eventoId, fila, columna);
    }

    private SeatPosition parsePerSeatKey(String key) {
        String[] parts = key.split(":");
        if (parts.length != 4) {
            return null;
        }
        if (!"evento".equals(parts[0]) || !"asiento".equals(parts[2])) {
            return null;
        }
        try {
            long eventId = Long.parseLong(parts[1]);
            String[] seatParts = parts[3].split("-");
            if (seatParts.length != 2) {
                return null;
            }
            int fila = Integer.parseInt(seatParts[0]);
            int columna = Integer.parseInt(seatParts[1]);
            return new SeatPosition(eventId, fila, columna);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private record SeatPosition(long eventoId, int fila, int columna) {}
}
