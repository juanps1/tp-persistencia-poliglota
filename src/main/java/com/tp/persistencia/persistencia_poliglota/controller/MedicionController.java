package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.nosql.Medicion;
import com.tp.persistencia.persistencia_poliglota.service.MedicionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/mediciones")
public class MedicionController {

    private final MedicionService medicionService;

    public MedicionController(MedicionService medicionService) {
        this.medicionService = medicionService;
    }

    @GetMapping("/{sensorId}")
    public List<Medicion> listarPorSensor(@PathVariable String sensorId) {
        return medicionService.listarPorSensor(sensorId);
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Medicion medicion) {
        if (medicion.getSensorId() == null || medicion.getSensorId().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "sensorId es requerido", "errors", Map.of("sensorId", "Campo obligatorio")));
        }
        if (medicion.getFechaHora() == null) {
            return ResponseEntity.status(400).body(
                Map.of("message", "fechaHora es requerida", "errors", Map.of("fechaHora", "Campo obligatorio (ISO8601)")));
        }
        if (medicion.getTemperatura() < -100 || medicion.getTemperatura() > 100) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Temperatura fuera de rango", "errors", Map.of("temperatura", "Debe estar entre -100 y 100")));
        }
        if (medicion.getHumedad() < 0 || medicion.getHumedad() > 100) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Humedad fuera de rango", "errors", Map.of("humedad", "Debe estar entre 0 y 100")));
        }
        Medicion guardada = medicionService.guardarMedicion(medicion);
        return ResponseEntity.status(201).body(guardada);
    }
}
