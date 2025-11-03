package com.tp.persistencia.persistencia_poliglota.service;

import com.tp.persistencia.persistencia_poliglota.model.sql.Factura;
import com.tp.persistencia.persistencia_poliglota.repository.FacturaRepository;
import com.tp.persistencia.persistencia_poliglota.repository.AlertaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final AlertaRepository alertaRepository;

    public FacturaService(FacturaRepository facturaRepository, AlertaRepository alertaRepository) {
        this.facturaRepository = facturaRepository;
        this.alertaRepository = alertaRepository;
    }

    public List<Factura> listar() {
        return facturaRepository.findAll();
    }

    public Factura guardar(Factura factura) {
        // Si la factura se marca como pagada → borrar la alerta
        if ("pagada".equalsIgnoreCase(factura.getEstado()) && factura.getUsuario() != null) {
            alertaRepository.deleteAll(alertaRepository.findByUsuarioId(factura.getUsuario().getId()));
        }

        return facturaRepository.save(factura);
    }
}
