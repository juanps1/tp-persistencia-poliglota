package com.tp.persistencia.persistencia_poliglota.repository;
import com.tp.persistencia.persistencia_poliglota.model.sql.SolicitudProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;

import java.util.List;

public interface SolicitudProcesoRepository extends JpaRepository<SolicitudProceso, Long> {
    List<SolicitudProceso> findByUsuarioId(Long usuarioId);
    List<SolicitudProceso> findByEstado(String estado);
    List<SolicitudProceso> findByUsuarioIdAndEstadoAndFechaFinalizacionBetween(
            Long usuarioId, String estado, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
