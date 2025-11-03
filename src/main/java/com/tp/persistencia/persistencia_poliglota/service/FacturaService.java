package com.tp.persistencia.persistencia_poliglota.service;


import com.tp.persistencia.persistencia_poliglota.model.sql.Factura;
import com.tp.persistencia.persistencia_poliglota.repository.FacturaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<Factura> listar() {
        return facturaRepository.findAll();
    }

    public Factura guardar(Factura factura) {
        return facturaRepository.save(factura);
    }
}
