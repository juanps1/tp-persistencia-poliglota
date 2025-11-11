package com.tp.persistencia.persistencia_poliglota.repository;

import com.tp.persistencia.persistencia_poliglota.model.sql.Alerta;
import com.tp.persistencia.persistencia_poliglota.model.sql.EstadoAlerta;
import com.tp.persistencia.persistencia_poliglota.model.sql.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface AlertaRepository extends JpaRepository<Alerta, Long>, JpaSpecificationExecutor<Alerta> {
    long countByEstado(EstadoAlerta estado);
    long countByEstadoAndTipo(EstadoAlerta estado, TipoAlerta tipo);

    @Query("select count(a) from Alerta a where a.fechaHora >= :desde and a.fechaHora < :hasta")
    long countByRango(Instant desde, Instant hasta);

    @Query("select count(a) from Alerta a where a.estado = 'RESUELTA' and a.resueltaEn >= :desde")
    long countResueltasDesde(Instant desde);
}
