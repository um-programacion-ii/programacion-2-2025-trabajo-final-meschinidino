package com.eventos.sistemaeventos.application.service;

import com.eventos.sistemaeventos.application.dto.catedra.VentaCatedraRequestDTO;
import com.eventos.sistemaeventos.application.dto.catedra.VentaCatedraResponseDTO;
import com.eventos.sistemaeventos.application.port.external.ProxyGateway;
import com.eventos.sistemaeventos.application.port.repository.VentaRepositoryPort;
import com.eventos.sistemaeventos.domain.AsientoSesion;
import com.eventos.sistemaeventos.domain.Evento;
import com.eventos.sistemaeventos.domain.Sesion;
import com.eventos.sistemaeventos.domain.Venta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepositoryPort ventaRepository;

    @Mock
    private SesionService sesionService;

    @Mock
    private EventoService eventoService;

    @Mock
    private ProxyGateway proxyGateway;

    @InjectMocks
    private VentaService ventaService;

    @Test
    void realizarVenta_persistsVentaAndClearsSession() {
        String username = "dino";
        Sesion sesion = new Sesion();
        sesion.setSessionId("s-1");
        sesion.setUsername(username);
        sesion.setEventoId(1L);
        sesion.setPaso("SELECCION_ASIENTOS");
        sesion.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        sesion.setLastActivity(LocalDateTime.now().minusMinutes(1));

        AsientoSesion asiento = new AsientoSesion();
        asiento.setFila(2);
        asiento.setColumna(3);
        asiento.setPersona("Fernando");
        sesion.getAsientosSeleccionados().add(asiento);

        when(sesionService.obtenerSesion(username)).thenReturn(sesion);

        Evento evento = new Evento();
        evento.setId(1L);
        evento.setPrecioEntrada(new BigDecimal("1400.10"));
        when(eventoService.obtenerEvento(1L)).thenReturn(evento);

        VentaCatedraResponseDTO.AsientoVentaDTO asientoResponse = new VentaCatedraResponseDTO.AsientoVentaDTO();
        asientoResponse.setFila(2);
        asientoResponse.setColumna(3);
        asientoResponse.setPersona("Fernando");
        asientoResponse.setEstado("Vendido");

        VentaCatedraResponseDTO response = new VentaCatedraResponseDTO();
        response.setEventoId(1L);
        response.setVentaId(1506L);
        response.setFechaVenta(OffsetDateTime.now(ZoneOffset.UTC));
        response.setPrecioVenta(new BigDecimal("1400.10"));
        response.setResultado(true);
        response.setDescripcion("Venta realizada con exito");
        response.setAsientos(List.of(asientoResponse));

        when(proxyGateway.realizarVenta(any(VentaCatedraRequestDTO.class))).thenReturn(response);
        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> {
            Venta venta = invocation.getArgument(0);
            venta.setId(10L);
            return venta;
        });

        Venta venta = ventaService.realizarVenta(username);

        assertThat(venta.getId()).isEqualTo(10L);
        assertThat(venta.getVentaIdCatedra()).isEqualTo(1506L);
        assertThat(venta.getEstadoSincronizacion()).isEqualTo("CONFIRMADA");
        assertThat(venta.getAsientos()).hasSize(1);

        ArgumentCaptor<VentaCatedraRequestDTO> requestCaptor = ArgumentCaptor.forClass(VentaCatedraRequestDTO.class);
        verify(proxyGateway).realizarVenta(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getEventoId()).isEqualTo(1L);
        assertThat(requestCaptor.getValue().getPrecioVenta()).isEqualTo(new BigDecimal("1400.10"));
        assertThat(requestCaptor.getValue().getAsientos()).hasSize(1);

        verify(sesionService).eliminarSesion(username);
    }

    @Test
    void realizarVenta_withoutSelectedSeats_throws() {
        String username = "dino";
        Sesion sesion = new Sesion();
        sesion.setSessionId("s-2");
        sesion.setUsername(username);
        sesion.setEventoId(1L);
        sesion.setPaso("SELECCION_ASIENTOS");
        sesion.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        sesion.setLastActivity(LocalDateTime.now().minusMinutes(1));

        when(sesionService.obtenerSesion(username)).thenReturn(sesion);

        assertThatThrownBy(() -> ventaService.realizarVenta(username))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No hay asientos seleccionados");
    }
}
