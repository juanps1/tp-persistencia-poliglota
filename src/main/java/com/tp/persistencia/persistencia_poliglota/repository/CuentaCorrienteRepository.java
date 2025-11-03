package com.tp.persistencia.persistencia_poliglota.repository;


import com.tp.persistencia.persistencia_poliglota.model.sql.CuentaCorriente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaCorrienteRepository extends JpaRepository<CuentaCorriente, Long> {
}
