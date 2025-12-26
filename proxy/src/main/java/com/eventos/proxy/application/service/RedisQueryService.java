package com.eventos.proxy.application.service;

import com.eventos.proxy.application.port.in.RedisQueryUseCase;
import com.eventos.proxy.application.port.out.RedisSeatStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class RedisQueryService implements RedisQueryUseCase {

    private final RedisSeatStore redisSeatStore;

    public RedisQueryService(RedisSeatStore redisSeatStore) {
        this.redisSeatStore = redisSeatStore;
    }

    @Override
    public Map<Object, Object> obtenerEstadoAsientos(Long eventoId) {
        log.debug("Consultando asientos del evento {}", eventoId);
        return redisSeatStore.obtenerEstadoAsientos(eventoId);
    }

    @Override
    public String obtenerEstadoAsiento(Long eventoId, int fila, int columna) {
        Object estado = redisSeatStore.obtenerEstadoAsiento(eventoId, fila, columna);
        return estado != null ? estado.toString() : "Desconocido";
    }
}
