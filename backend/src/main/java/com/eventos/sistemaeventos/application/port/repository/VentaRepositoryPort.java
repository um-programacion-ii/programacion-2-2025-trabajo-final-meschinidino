package com.eventos.sistemaeventos.application.port.repository;

import com.eventos.sistemaeventos.domain.Venta;

import java.util.List;
import java.util.Optional;

public interface VentaRepositoryPort {

    Venta save(Venta venta);

    List<Venta> findByUsernameOrderByFechaVentaDesc(String username);

    Optional<Venta> findById(Long id);

    List<Venta> findByEstadoSincronizacion(String estado);
}
