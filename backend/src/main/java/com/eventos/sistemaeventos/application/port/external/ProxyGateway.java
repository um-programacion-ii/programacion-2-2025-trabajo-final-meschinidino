package com.eventos.sistemaeventos.application.port.external;

import com.eventos.sistemaeventos.application.dto.catedra.BloquearAsientosRequestDTO;
import com.eventos.sistemaeventos.application.dto.catedra.BloquearAsientosResponseDTO;
import com.eventos.sistemaeventos.application.dto.catedra.EventoCatedraDTO;
import com.eventos.sistemaeventos.application.dto.catedra.EventoResumidoCatedraDTO;
import com.eventos.sistemaeventos.application.dto.catedra.VentaCatedraRequestDTO;
import com.eventos.sistemaeventos.application.dto.catedra.VentaCatedraResponseDTO;
import com.eventos.sistemaeventos.application.dto.catedra.VentaResumidaCatedraDTO;

import java.util.List;

public interface ProxyGateway {

    List<EventoResumidoCatedraDTO> listarEventosResumidos();

    List<EventoCatedraDTO> listarEventosCompletos();

    EventoCatedraDTO obtenerEvento(Long eventoId);

    BloquearAsientosResponseDTO bloquearAsientos(BloquearAsientosRequestDTO request);

    VentaCatedraResponseDTO realizarVenta(VentaCatedraRequestDTO request);

    List<VentaResumidaCatedraDTO> listarVentas();

    VentaCatedraResponseDTO obtenerVenta(Long ventaId);

    Object obtenerAsientosEvento(Long eventoId);
}
