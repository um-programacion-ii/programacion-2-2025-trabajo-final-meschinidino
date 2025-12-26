package com.eventos.sistemaeventos.infrastructure.persistence;

import com.eventos.sistemaeventos.application.port.repository.VentaRepositoryPort;
import com.eventos.sistemaeventos.domain.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long>, VentaRepositoryPort {
    
    List<Venta> findByUsername(String username);
    
    List<Venta> findByEventoId(Long eventoId);
    
    List<Venta> findByResultadoTrue();
    
    List<Venta> findByEstadoSincronizacion(String estado);
    
    List<Venta> findByUsernameOrderByFechaVentaDesc(String username);
}
