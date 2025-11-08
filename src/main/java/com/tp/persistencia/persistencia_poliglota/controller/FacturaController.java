package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Factura;
import com.tp.persistencia.persistencia_poliglota.model.nosql.Alerta;
import com.tp.persistencia.persistencia_poliglota.repository.AlertaRepository;
import com.tp.persistencia.persistencia_poliglota.service.FacturaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;

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
    public ResponseEntity<?> guardar(@RequestBody Factura factura) {
        if (factura.getUsuario() == null || factura.getUsuario().getId() == null) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Usuario es requerido", "errors", Map.of("usuario", "Debe enviar objeto {id}")));
        }
        if (factura.getEstado() == null || factura.getEstado().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Estado es requerido", "errors", Map.of("estado", "Campo obligatorio")));
        }
        Factura nuevaFactura = facturaService.guardar(factura);
        if ("vencida".equalsIgnoreCase(nuevaFactura.getEstado()) ||
            "impaga".equalsIgnoreCase(nuevaFactura.getEstado())) {
            Alerta alerta = new Alerta();
            alerta.setUsuarioId(nuevaFactura.getUsuario() != null ? nuevaFactura.getUsuario().getId() : null);
            alerta.setMensaje("Factura " + nuevaFactura.getId() + " está " + nuevaFactura.getEstado());
            alerta.setNivel("ALTA");
            alertaRepository.save(alerta);
        }
        return ResponseEntity.status(201).body(nuevaFactura);
    }
}
