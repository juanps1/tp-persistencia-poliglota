package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.*;
import com.tp.persistencia.persistencia_poliglota.service.AlertaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public ResponseEntity<Page<Alerta>> listar(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String sensorId,
            @RequestParam(required = false) Instant desde,
            @RequestParam(required = false) Instant hasta,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort
    ) {
        Sort sortObj = resolveSort(sort);
        Pageable pageable = PageRequest.of(page, size, sortObj == null ? Sort.by(Sort.Direction.DESC, "fechaHora") : sortObj);
        Page<Alerta> result = alertaService.listar(tipo, estado, sensorId, desde, hasta, search, pageable);
        return ResponseEntity.ok(result);
    }

    private Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "fechaHora");
        String[] parts = sort.split(",");
        if (parts.length == 2) {
            return Sort.by("desc".equalsIgnoreCase(parts[1]) ? Sort.Direction.DESC : Sort.Direction.ASC, parts[0]);
        }
        return Sort.by(sort).descending();
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> metrics() {
        return ResponseEntity.ok(alertaService.metrics());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {
        Alerta a = alertaService.detalle(id);
        if (a == null) return ResponseEntity.status(404).body(Map.of("message", "Alerta no encontrada"));
        return ResponseEntity.ok(a);
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<?> resolver(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body, @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId) {
        if (usuarioId == null) usuarioId = -1L; // fallback simple
        String comentario = body == null ? null : (String) body.get("comentario");
        Alerta a = alertaService.resolver(id, usuarioId, comentario);
        if (a == null) return ResponseEntity.status(404).body(Map.of("message", "Alerta no encontrada"));
        return ResponseEntity.ok(a);
    }

    @PatchMapping("/{id}/reabrir")
    public ResponseEntity<?> reabrir(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body, @RequestHeader(value = "X-Usuario-Id", required = false) Long usuarioId) {
        if (usuarioId == null) usuarioId = -1L;
        String motivo = body == null ? null : (String) body.get("motivo");
        Alerta a = alertaService.reabrir(id, usuarioId, motivo);
        if (a == null) return ResponseEntity.status(404).body(Map.of("message", "Alerta no encontrada"));
        return ResponseEntity.ok(a);
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        String tipoStr = (String) body.get("tipo");
        String descripcion = (String) body.get("descripcion");
        String sensorId = (String) body.get("sensorId");
        String severidadStr = (String) body.get("severidad");
        String origen = (String) body.get("origen");
        if (tipoStr == null || descripcion == null || descripcion.isBlank()) {
            return ResponseEntity.status(400).body(Map.of("message", "tipo y descripcion son requeridos"));
        }
        TipoAlerta tipoEnum;
        try { tipoEnum = TipoAlerta.valueOf(tipoStr.toUpperCase()); } catch (Exception e) { return ResponseEntity.status(400).body(Map.of("message", "tipo inválido")); }
        if (tipoEnum == TipoAlerta.SENSOR && (sensorId == null || sensorId.isBlank())) {
            return ResponseEntity.status(400).body(Map.of("message", "sensorId requerido para tipo SENSOR"));
        }
        if (tipoEnum == TipoAlerta.CLIMATICA) sensorId = null;
        Severidad sev = null;
        if (severidadStr != null) {
            try { sev = Severidad.valueOf(severidadStr.toUpperCase()); } catch (Exception ignored) {}
        }
        Alerta a = alertaService.crear(tipoEnum, sensorId, descripcion, sev, origen);
        return ResponseEntity.status(201).body(a);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        boolean ok = alertaService.eliminar(id);
        if (!ok) return ResponseEntity.status(404).body(Map.of("message", "Alerta no encontrada"));
        return ResponseEntity.ok(Map.of("message", "Alerta eliminada", "id", id));
    }
}
