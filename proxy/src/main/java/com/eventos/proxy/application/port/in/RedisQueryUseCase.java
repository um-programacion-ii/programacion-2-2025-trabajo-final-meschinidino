package com.eventos.proxy.application.port.in;

import java.util.Map;

public interface RedisQueryUseCase {

    Map<Object, Object> obtenerEstadoAsientos(Long eventoId);

    String obtenerEstadoAsiento(Long eventoId, int fila, int columna);
}
