package com.tp.persistencia.persistencia_poliglota.repository;
import com.tp.persistencia.persistencia_poliglota.model.sql.SolicitudProceso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudProcesoRepository extends JpaRepository<SolicitudProceso, Long> {
}
