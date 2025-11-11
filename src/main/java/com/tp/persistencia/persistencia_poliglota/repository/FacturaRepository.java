package com.tp.persistencia.persistencia_poliglota.repository;


import com.tp.persistencia.persistencia_poliglota.model.sql.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByUsuarioId(Long usuarioId);
    List<Factura> findByEstado(String estado);
    List<Factura> findByUsuarioIdAndEstado(Long usuarioId, String estado);
}
