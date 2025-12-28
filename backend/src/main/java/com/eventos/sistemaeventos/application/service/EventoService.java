package com.eventos.sistemaeventos.application.service;

import com.eventos.sistemaeventos.application.dto.catedra.EventoCatedraDTO;
import com.eventos.sistemaeventos.application.port.external.ProxyGateway;
import com.eventos.sistemaeventos.application.port.repository.EventoRepositoryPort;
import com.eventos.sistemaeventos.domain.Evento;
import com.eventos.sistemaeventos.domain.EventoTipo;
import com.eventos.sistemaeventos.domain.Integrante;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventoService {

    private final EventoRepositoryPort eventoRepository;
    private final ProxyGateway proxyGateway;

    @Transactional
    public void sincronizarEventos() {
        log.info("Iniciando sincronizacion de eventos a traves del PROXY");

        try {
            List<EventoCatedraDTO> eventosDTO = proxyGateway.listarEventosCompletos();

            for (EventoCatedraDTO dto : eventosDTO) {
                Evento evento = eventoRepository.findById(dto.getId())
                        .orElse(new Evento());

                mapearDTOAEvento(dto, evento);
                eventoRepository.save(evento);
            }

            log.info("Sincronizacion completada: {} eventos actualizados", eventosDTO.size());

        } catch (Exception e) {
            log.error("Error sincronizando eventos: {}", e.getMessage());
            throw new RuntimeException("Error en sincronizacion de eventos", e);
        }
    }

    public List<Evento> listarEventosActivos() {
        return eventoRepository.findByActivoTrue();
    }

    public Evento obtenerEvento(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado: " + id));
    }

    public Object obtenerAsientosEvento(Long eventoId) {
        return proxyGateway.obtenerAsientosEvento(eventoId);
    }

    @Transactional
    public Evento sincronizarEvento(Long eventoId) {
        log.info("Sincronizando evento {} a traves del PROXY", eventoId);

        try {
            EventoCatedraDTO dto = proxyGateway.obtenerEvento(eventoId);

            Evento evento = eventoRepository.findById(dto.getId())
                    .orElse(new Evento());

            mapearDTOAEvento(dto, evento);
            return eventoRepository.save(evento);

        } catch (Exception e) {
            log.error("Error sincronizando evento {}: {}", eventoId, e.getMessage());
            throw new RuntimeException("Error sincronizando evento", e);
        }
    }

    @Transactional
    public void procesarCambioEvento(String tipoCambio, EventoCatedraDTO dto) {
        String normalized = tipoCambio.trim().toUpperCase();

        switch (normalized) {
            case "NEW", "UPDATE" -> {
                Evento evento = eventoRepository.findById(dto.getId())
                        .orElse(new Evento());
                mapearDTOAEvento(dto, evento);
                eventoRepository.save(evento);
            }
            case "DELETE" -> eventoRepository.findById(dto.getId())
                    .ifPresent(evento -> {
                        evento.setActivo(false);
                        eventoRepository.save(evento);
                    });
            default -> throw new RuntimeException("tipo_cambio inválido: " + tipoCambio);
        }
    }

    private void mapearDTOAEvento(EventoCatedraDTO dto, Evento evento) {
        evento.setId(dto.getId());
        evento.setTitulo(dto.getTitulo());
        evento.setResumen(dto.getResumen());
        evento.setDescripcion(dto.getDescripcion());
        if (dto.getFecha() != null) {
            evento.setFecha(dto.getFecha().toLocalDateTime());
        }
        evento.setDireccion(dto.getDireccion());
        evento.setImagen(dto.getImagen());
        evento.setFilaAsientos(dto.getFilaAsientos());
        evento.setColumnaAsientos(dto.getColumnaAsientos());
        evento.setPrecioEntrada(dto.getPrecioEntrada());

        if (dto.getEventoTipo() != null) {
            EventoTipo tipo = new EventoTipo();
            tipo.setNombre(dto.getEventoTipo().getNombre());
            tipo.setDescripcion(dto.getEventoTipo().getDescripcion());
            evento.setEventoTipo(tipo);
        }

        if (dto.getIntegrantes() != null) {
            List<Integrante> integrantes = dto.getIntegrantes().stream()
                    .map(integranteDTO -> {
                        Integrante integrante = new Integrante();
                        integrante.setNombre(integranteDTO.getNombre());
                        integrante.setApellido(integranteDTO.getApellido());
                        integrante.setIdentificacion(integranteDTO.getIdentificacion());
                        return integrante;
                    })
                    .collect(Collectors.toList());

            evento.getIntegrantes().clear();
            evento.getIntegrantes().addAll(integrantes);
        }

        evento.setActivo(true);
        evento.setUltimaSincronizacion(LocalDateTime.now());
    }
}
