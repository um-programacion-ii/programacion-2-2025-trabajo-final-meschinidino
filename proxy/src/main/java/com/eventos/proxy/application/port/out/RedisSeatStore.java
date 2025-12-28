package com.eventos.proxy.application.port.out;

import java.util.Map;

public interface RedisSeatStore {

    Map<Object, Object> obtenerEstadoAsientos(Long eventoId);

    Object obtenerEstadoAsiento(Long eventoId, int fila, int columna);
}
