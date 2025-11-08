package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.nosql.Alerta;
import com.tp.persistencia.persistencia_poliglota.repository.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    @Autowired
    private AlertaRepository alertaRepository;

    @PostMapping
    public ResponseEntity<?> crearAlerta(@RequestBody Alerta alerta) {
        if (alerta.getUsuarioId() == null) {
            return ResponseEntity.status(400).body(
                Map.of("message", "usuarioId es requerido", "errors", Map.of("usuarioId", "Campo obligatorio")));
        }
        if (alerta.getMensaje() == null || alerta.getMensaje().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Mensaje es requerido", "errors", Map.of("mensaje", "Campo obligatorio")));
        }
        Alerta guardada = alertaRepository.save(alerta);
        return ResponseEntity.status(201).body(guardada);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<List<Alerta>> obtenerAlertasPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(alertaRepository.findByUsuarioId(usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<Alerta>> obtenerTodasLasAlertas() {
        return ResponseEntity.ok(alertaRepository.findAll());
    }
}
