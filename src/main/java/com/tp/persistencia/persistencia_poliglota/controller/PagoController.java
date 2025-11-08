package com.tp.persistencia.persistencia_poliglota.controller;


import com.tp.persistencia.persistencia_poliglota.model.sql.Pago;
import com.tp.persistencia.persistencia_poliglota.service.PagoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @GetMapping
    public List<Pago> listar() {
        return pagoService.listar();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Pago pago) {
        if (pago.getMontoPagado() <= 0) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Monto inválido", "errors", Map.of("montoPagado", "Debe ser mayor a 0")));
        }
        if (pago.getFactura() == null || pago.getFactura().getId() == null) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Factura es requerida", "errors", Map.of("factura", "Debe enviar objeto {id}")));
        }
        Pago guardado = pagoService.guardar(pago);
        return ResponseEntity.status(201).body(guardado);
    }
}
