package com.eventos.proxy.application.port.out;

import java.util.List;
import java.util.Map;

public interface CatedraApiClient {

    List<Map<String, Object>> obtenerEventosResumidos();

    List<Map<String, Object>> obtenerEventosCompletos();

    Map<String, Object> obtenerEvento(Long eventoId);

    Map<String, Object> bloquearAsientos(Map<String, Object> request);

    Map<String, Object> realizarVenta(Map<String, Object> request);

    List<Map<String, Object>> listarVentas();

    Map<String, Object> obtenerVenta(Long ventaId);
}
