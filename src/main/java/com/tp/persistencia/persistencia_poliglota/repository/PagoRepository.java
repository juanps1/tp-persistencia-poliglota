package com.tp.persistencia.persistencia_poliglota.repository;


import com.tp.persistencia.persistencia_poliglota.model.sql.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface PagoRepository extends JpaRepository<Pago, Long> {
	List<Pago> findByFacturaId(Long facturaId);
}
