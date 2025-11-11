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
    /**
     * Listar todos los pagos
     */

    @GetMapping
    public ResponseEntity<List<Pago>> listarTodos() {
        return ResponseEntity.ok(pagoService.listarTodos());
    }

    /**
     * Listar pagos de una factura
     */
    @GetMapping("/factura/{facturaId}")
    public ResponseEntity<List<Pago>> listarPorFactura(@PathVariable Long facturaId) {
        return ResponseEntity.ok(pagoService.listarPorFactura(facturaId));
    }

    /**
     * Obtener un pago por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return pagoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Registrar un nuevo pago para una factura
     * Body: { "facturaId": 1, "montoPagado": 450.0, "metodoPago": "tarjeta_credito" }
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarPago(@RequestBody Map<String, Object> request) {
        try {
            Long facturaId = Long.valueOf(request.get("facturaId").toString());
            Double montoPagado = Double.valueOf(request.get("montoPagado").toString());
            String metodoPago = request.get("metodoPago").toString();

            Pago pago = pagoService.registrarPago(facturaId, montoPagado, metodoPago);
            
            // Obtener la factura actualizada para devolverla en la respuesta
            var facturaActualizada = pago.getFactura();
            
            return ResponseEntity.status(201).body(Map.of(
                "pago", pago,
                "facturaActualizada", facturaActualizada
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Eliminar pago
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        pagoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
