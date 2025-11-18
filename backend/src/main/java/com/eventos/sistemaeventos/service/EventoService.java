package com.eventos.sistemaeventos.service;

import com.eventos.sistemaeventos.domain.Evento;
import com.eventos.sistemaeventos.domain.EventoTipo;
import com.eventos.sistemaeventos.domain.Integrante;
import com.eventos.sistemaeventos.dto.catedra.EventoCatedraDTO;
import com.eventos.sistemaeventos.dto.catedra.EventoResumidoCatedraDTO;
import com.eventos.sistemaeventos.repository.EventoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar eventos localmente
 * Referencia: Sección 4.2 del documento
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventoService {
    
    private final EventoRepository eventoRepository;
    private final CatedraService catedraService;
    
    /**
     * Sincroniza eventos desde cátedra
     */
    @Transactional
    public void sincronizarEventos() {
        log.info("Iniciando sincronización de eventos");
        
        try {
            List<EventoCatedraDTO> eventosDTO = catedraService.listarEventosCompletos();
            
            for (EventoCatedraDTO dto : eventosDTO) {
                Evento evento = eventoRepository.findById(dto.getId())
                        .orElse(new Evento());
                
                mapearDTOAEvento(dto, evento);
                eventoRepository.save(evento);
            }
            
            log.info("Sincronización completada: {} eventos actualizados", eventosDTO.size());
            
        } catch (Exception e) {
            log.error("Error sincronizando eventos: {}", e.getMessage());
            throw new RuntimeException("Error en sincronización de eventos", e);
        }
    }
    
    /**
     * Obtiene todos los eventos activos locales
     */
    public List<Evento> listarEventosActivos() {
        return eventoRepository.findByActivoTrue();
    }
    
    /**
     * Obtiene un evento por ID
     */
    public Evento obtenerEvento(Long id) {
        return eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado: " + id));
    }
    
    /**
     * Sincroniza un evento específico desde cátedra
     */
    @Transactional
    public Evento sincronizarEvento(Long eventoId) {
        log.info("Sincronizando evento {}", eventoId);
        
        try {
            EventoCatedraDTO dto = catedraService.obtenerEvento(eventoId);
            
            Evento evento = eventoRepository.findById(dto.getId())
                    .orElse(new Evento());
            
            mapearDTOAEvento(dto, evento);
            return eventoRepository.save(evento);
            
        } catch (Exception e) {
            log.error("Error sincronizando evento {}: {}", eventoId, e.getMessage());
            throw new RuntimeException("Error sincronizando evento", e);
        }
    }
    
    /**
     * Mapea DTO de cátedra a entidad local
     */
    private void mapearDTOAEvento(EventoCatedraDTO dto, Evento evento) {
        evento.setId(dto.getId());
        evento.setTitulo(dto.getTitulo());
        evento.setResumen(dto.getResumen());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFecha(dto.getFecha());
        evento.setDireccion(dto.getDireccion());
        evento.setImagen(dto.getImagen());
        evento.setFilaAsientos(dto.getFilaAsientos());
        evento.setColumnaAsientos(dto.getColumnaAsientos());
        evento.setPrecioEntrada(dto.getPrecioEntrada());
        
        // Mapear tipo de evento
        if (dto.getEventoTipo() != null) {
            EventoTipo tipo = new EventoTipo();
            tipo.setNombre(dto.getEventoTipo().getNombre());
            tipo.setDescripcion(dto.getEventoTipo().getDescripcion());
            evento.setEventoTipo(tipo);
        }
        
        // Mapear integrantes
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

