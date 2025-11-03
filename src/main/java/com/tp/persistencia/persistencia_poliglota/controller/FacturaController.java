package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Factura;
import com.tp.persistencia.persistencia_poliglota.model.nosql.Alerta;
import com.tp.persistencia.persistencia_poliglota.repository.AlertaRepository;
import com.tp.persistencia.persistencia_poliglota.service.FacturaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;
    private final AlertaRepository alertaRepository;

    public FacturaController(FacturaService facturaService, AlertaRepository alertaRepository) {
        this.facturaService = facturaService;
        this.alertaRepository = alertaRepository;
    }

    @GetMapping
    public List<Factura> listar() {
        return facturaService.listar();
    }

    @PostMapping
    public Factura guardar(@RequestBody Factura factura) {
        // Guardamos la factura normalmente en SQL
        Factura nuevaFactura = facturaService.guardar(factura);

        // Si está vencida o impaga, generamos una alerta en MongoDB
        if ("vencida".equalsIgnoreCase(nuevaFactura.getEstado()) ||
            "impaga".equalsIgnoreCase(nuevaFactura.getEstado())) {

            Alerta alerta = new Alerta();
            alerta.setUsuarioId(nuevaFactura.getUsuario() != null ? nuevaFactura.getUsuario().getId() : null);
            alerta.setMensaje("Factura " + nuevaFactura.getId() + " está " + nuevaFactura.getEstado());
            alerta.setNivel("ALTA");

            alertaRepository.save(alerta);
        }

        // Devolvemos la factura guardada
        return nuevaFactura;
    }
}
