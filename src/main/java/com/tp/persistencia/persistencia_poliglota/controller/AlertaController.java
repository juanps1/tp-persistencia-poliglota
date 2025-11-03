package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.nosql.Alerta;
import com.tp.persistencia.persistencia_poliglota.repository.AlertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    @Autowired
    private AlertaRepository alertaRepository;

    @PostMapping
    public ResponseEntity<Alerta> crearAlerta(@RequestBody Alerta alerta) {
        return ResponseEntity.ok(alertaRepository.save(alerta));
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
