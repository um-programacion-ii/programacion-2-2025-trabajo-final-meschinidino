package com.eventos.proxy.application.service;

import com.eventos.proxy.application.port.in.CatedraProxyUseCase;
import com.eventos.proxy.application.port.out.CatedraApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CatedraProxyService implements CatedraProxyUseCase {

    private final CatedraApiClient catedraApiClient;

    public CatedraProxyService(CatedraApiClient catedraApiClient) {
        this.catedraApiClient = catedraApiClient;
    }

    @Override
    public List<Map<String, Object>> obtenerEventosResumidos() {
        log.info("[PROXY] Obteniendo eventos resumidos desde catedra");
        return catedraApiClient.obtenerEventosResumidos();
    }

    @Override
    public List<Map<String, Object>> obtenerEventosCompletos() {
        log.info("[PROXY] Obteniendo eventos completos desde catedra");
        return catedraApiClient.obtenerEventosCompletos();
    }

    @Override
    public Map<String, Object> obtenerEvento(Long eventoId) {
        log.info("[PROXY] Obteniendo evento {} desde catedra", eventoId);
        return catedraApiClient.obtenerEvento(eventoId);
    }

    @Override
    public Map<String, Object> bloquearAsientos(Map<String, Object> request) {
        log.info("[PROXY] Bloqueando asientos en catedra");
        return catedraApiClient.bloquearAsientos(request);
    }

    @Override
    public Map<String, Object> realizarVenta(Map<String, Object> request) {
        log.info("[PROXY] Realizando venta en catedra");
        return catedraApiClient.realizarVenta(request);
    }

    @Override
    public List<Map<String, Object>> listarVentas() {
        log.info("[PROXY] Listando ventas desde catedra");
        return catedraApiClient.listarVentas();
    }

    @Override
    public Map<String, Object> obtenerVenta(Long ventaId) {
        log.info("[PROXY] Obteniendo venta {} desde catedra", ventaId);
        return catedraApiClient.obtenerVenta(ventaId);
    }
}
