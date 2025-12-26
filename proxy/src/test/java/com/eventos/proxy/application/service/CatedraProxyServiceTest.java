package com.eventos.proxy.application.service;

import com.eventos.proxy.application.port.out.CatedraApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatedraProxyServiceTest {

    @Mock
    private CatedraApiClient catedraApiClient;

    @InjectMocks
    private CatedraProxyService catedraProxyService;

    @Test
    void obtenerEventosResumidos_delegaEnCliente() {
        List<Map<String, Object>> expected = List.of(Map.of("id", 1));
        when(catedraApiClient.obtenerEventosResumidos()).thenReturn(expected);

        List<Map<String, Object>> result = catedraProxyService.obtenerEventosResumidos();

        assertThat(result).isSameAs(expected);
        verify(catedraApiClient).obtenerEventosResumidos();
    }

    @Test
    void obtenerEventosCompletos_delegaEnCliente() {
        List<Map<String, Object>> expected = List.of(Map.of("id", 2));
        when(catedraApiClient.obtenerEventosCompletos()).thenReturn(expected);

        List<Map<String, Object>> result = catedraProxyService.obtenerEventosCompletos();

        assertThat(result).isSameAs(expected);
        verify(catedraApiClient).obtenerEventosCompletos();
    }

    @Test
    void obtenerEvento_delegaEnCliente() {
        Map<String, Object> expected = Map.of("id", 3);
        when(catedraApiClient.obtenerEvento(3L)).thenReturn(expected);

        Map<String, Object> result = catedraProxyService.obtenerEvento(3L);

        assertThat(result).isSameAs(expected);
        verify(catedraApiClient).obtenerEvento(3L);
    }

    @Test
    void bloquearAsientos_delegaEnCliente() {
        Map<String, Object> request = Map.of("eventoId", 10);
        Map<String, Object> expected = Map.of("ok", true);
        when(catedraApiClient.bloquearAsientos(request)).thenReturn(expected);

        Map<String, Object> result = catedraProxyService.bloquearAsientos(request);

        assertThat(result).isSameAs(expected);
        verify(catedraApiClient).bloquearAsientos(request);
    }

    @Test
    void realizarVenta_delegaEnCliente() {
        Map<String, Object> request = Map.of("eventoId", 11);
        Map<String, Object> expected = Map.of("ventaId", 99);
        when(catedraApiClient.realizarVenta(request)).thenReturn(expected);

        Map<String, Object> result = catedraProxyService.realizarVenta(request);

        assertThat(result).isSameAs(expected);
        verify(catedraApiClient).realizarVenta(request);
    }

    @Test
    void listarVentas_delegaEnCliente() {
        List<Map<String, Object>> expected = List.of(Map.of("ventaId", 50));
        when(catedraApiClient.listarVentas()).thenReturn(expected);

        List<Map<String, Object>> result = catedraProxyService.listarVentas();

        assertThat(result).isSameAs(expected);
        verify(catedraApiClient).listarVentas();
    }

    @Test
    void obtenerVenta_delegaEnCliente() {
        Map<String, Object> expected = Map.of("ventaId", 77);
        when(catedraApiClient.obtenerVenta(77L)).thenReturn(expected);

        Map<String, Object> result = catedraProxyService.obtenerVenta(77L);

        assertThat(result).isSameAs(expected);
        verify(catedraApiClient).obtenerVenta(77L);
    }
}
