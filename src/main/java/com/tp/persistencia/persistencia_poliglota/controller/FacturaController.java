package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Factura;
import com.tp.persistencia.persistencia_poliglota.service.FacturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }
    /**
     * Listar todas las facturas
     */

    @GetMapping
    public ResponseEntity<List<Factura>> listarTodas() {
        return ResponseEntity.ok(facturaService.listarTodas());
    }

    /**
     * Listar facturas de un usuario
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Factura>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(facturaService.listarPorUsuario(usuarioId));
    }

    /**
     * Obtener una factura por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        return facturaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Generar factura para un usuario en un rango de fechas
     * Body: { "usuarioId": 1, "fechaInicio": "2025-10-01", "fechaFin": "2025-10-31" }
     * También acepta formato con hora: "2025-10-01T00:00:00"
     */
    @PostMapping("/generar")
    public ResponseEntity<?> generarFactura(@RequestBody Map<String, Object> request) {
        try {
            Long usuarioId = Long.valueOf(request.get("usuarioId").toString());
            
            // Parsear fechas - soporta yyyy-MM-dd o yyyy-MM-ddTHH:mm:ss
            String fechaInicioStr = request.get("fechaInicio").toString();
            String fechaFinStr = request.get("fechaFin").toString();
            
            LocalDateTime fechaInicio;
            LocalDateTime fechaFin;
            
            if (fechaInicioStr.length() == 10) { // formato yyyy-MM-dd
                fechaInicio = java.time.LocalDate.parse(fechaInicioStr).atStartOfDay();
            } else {
                fechaInicio = LocalDateTime.parse(fechaInicioStr);
            }
            
            if (fechaFinStr.length() == 10) { // formato yyyy-MM-dd
                fechaFin = java.time.LocalDate.parse(fechaFinStr).atTime(23, 59, 59);
            } else {
                fechaFin = LocalDateTime.parse(fechaFinStr);
            }

            Factura factura = facturaService.generarFacturaParaUsuario(usuarioId, fechaInicio, fechaFin);
            return ResponseEntity.status(201).body(factura);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Marcar factura como pagada
     */
    @PutMapping("/{id}/pagar")
    public ResponseEntity<?> marcarComoPagada(@PathVariable Long id) {
        try {
            Factura factura = facturaService.marcarComoPagada(id);
            return ResponseEntity.ok(factura);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualizar facturas vencidas (proceso manual o programado)
     */
    @PostMapping("/actualizar-vencidas")
    public ResponseEntity<?> actualizarVencidas() {
        facturaService.actualizarFacturasVencidas();
        return ResponseEntity.ok(Map.of("message", "Facturas vencidas actualizadas"));
    }

    /**
     * Verificar si un usuario tiene facturas vencidas
     */
    @GetMapping("/usuario/{usuarioId}/tiene-vencidas")
    public ResponseEntity<?> tieneFacturasVencidas(@PathVariable Long usuarioId) {
        boolean tieneVencidas = facturaService.tieneFacturasVencidas(usuarioId);
        return ResponseEntity.ok(Map.of("tieneFacturasVencidas", tieneVencidas));
    }

    /**
     * Eliminar factura
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        facturaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
