package com.eventos.sistemaeventos.application.service;

import com.eventos.sistemaeventos.application.dto.catedra.BloquearAsientosRequestDTO;
import com.eventos.sistemaeventos.application.dto.catedra.BloquearAsientosResponseDTO;
import com.eventos.sistemaeventos.application.port.external.ProxyGateway;
import com.eventos.sistemaeventos.application.port.repository.SesionRepositoryPort;
import com.eventos.sistemaeventos.domain.AsientoSesion;
import com.eventos.sistemaeventos.domain.Sesion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SesionServiceTest {

    @Mock
    private SesionRepositoryPort sesionRepository;

    @Mock
    private ProxyGateway proxyGateway;

    @InjectMocks
    private SesionService sesionService;

    @Test
    void bloquearAsientos_success_updatesSessionAndMarksAsientos() {
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

        when(sesionRepository.findByUsername(username)).thenReturn(Optional.of(sesion));
        when(sesionRepository.save(any(Sesion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BloquearAsientosResponseDTO.AsientoEstadoDTO estado = new BloquearAsientosResponseDTO.AsientoEstadoDTO();
        estado.setFila(2);
        estado.setColumna(3);
        estado.setEstado("Bloqueo exitoso");

        BloquearAsientosResponseDTO response = new BloquearAsientosResponseDTO();
        response.setResultado(true);
        response.setDescripcion("Asientos bloqueados con exito");
        response.setEventoId(1L);
        response.setAsientos(List.of(estado));

        when(proxyGateway.bloquearAsientos(any(BloquearAsientosRequestDTO.class))).thenReturn(response);

        BloquearAsientosResponseDTO result = sesionService.bloquearAsientos(username);

        assertThat(result.getResultado()).isTrue();
        assertThat(sesion.getPaso()).isEqualTo("CARGA_DATOS");
        assertThat(sesion.getAsientosSeleccionados())
                .allMatch(a -> Boolean.TRUE.equals(a.getBloqueadoEnCatedra()));

        ArgumentCaptor<BloquearAsientosRequestDTO> requestCaptor =
                ArgumentCaptor.forClass(BloquearAsientosRequestDTO.class);
        verify(proxyGateway).bloquearAsientos(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getEventoId()).isEqualTo(1L);
        assertThat(requestCaptor.getValue().getAsientos()).hasSize(1);

        verify(sesionRepository, atLeastOnce()).save(sesion);
    }

    @Test
    void bloquearAsientos_withoutEventoId_throws() {
        String username = "dino";
        Sesion sesion = new Sesion();
        sesion.setSessionId("s-2");
        sesion.setUsername(username);
        sesion.setPaso("SELECCION_ASIENTOS");
        sesion.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        sesion.setLastActivity(LocalDateTime.now().minusMinutes(1));

        when(sesionRepository.findByUsername(username)).thenReturn(Optional.of(sesion));
        when(sesionRepository.save(any(Sesion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> sesionService.bloquearAsientos(username))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No hay evento seleccionado");
    }

    @Test
    void bloquearAsientos_withoutAsientos_throws() {
        String username = "dino";
        Sesion sesion = new Sesion();
        sesion.setSessionId("s-3");
        sesion.setUsername(username);
        sesion.setEventoId(1L);
        sesion.setPaso("SELECCION_ASIENTOS");
        sesion.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        sesion.setLastActivity(LocalDateTime.now().minusMinutes(1));

        when(sesionRepository.findByUsername(username)).thenReturn(Optional.of(sesion));
        when(sesionRepository.save(any(Sesion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> sesionService.bloquearAsientos(username))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No hay asientos seleccionados");
    }
}
