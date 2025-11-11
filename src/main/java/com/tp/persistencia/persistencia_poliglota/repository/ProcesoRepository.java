package com.tp.persistencia.persistencia_poliglota.repository;

import com.tp.persistencia.persistencia_poliglota.model.sql.Proceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcesoRepository extends JpaRepository<Proceso, Long> {
    Optional<Proceso> findByTipo(String tipo);
}
