package com.tp.persistencia.persistencia_poliglota.controller;


import com.tp.persistencia.persistencia_poliglota.model.nosql.Sensor;
import com.tp.persistencia.persistencia_poliglota.service.SensorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/sensores")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @GetMapping
    public List<Sensor> listar() {
        return sensorService.listarSensores();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        String nombre = body == null ? null : (String) body.get("nombre");
        if (nombre == null || nombre.isBlank()) {
            return ResponseEntity.status(400).body(Map.of(
                "message", "Nombre es requerido",
                "errors", Map.of("nombre", "Campo obligatorio")));
        }
        if (sensorService.existeNombre(nombre)) {
            return ResponseEntity.status(409).body(Map.of(
                "message", "Nombre duplicado",
                "errors", Map.of("nombre", "Ya existe un sensor con ese nombre")));
        }
        String idGenerado = generarIdDesdeNombre(nombre);
        Sensor sensor = new Sensor();
        sensor.setId(idGenerado);
        sensor.setNombre(nombre);
        Sensor guardado = sensorService.guardarSensor(sensor);
        return ResponseEntity.status(201).body(Map.of("id", guardado.getId(), "nombre", guardado.getNombre()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> renombrar(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Sensor existente = sensorService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body(Map.of(
                "message", "Sensor no encontrado",
                "errors", Map.of("id", "No existe sensor con ese id")));
        }
        String nombre = body == null ? null : (String) body.get("nombre");
        if (nombre == null || nombre.isBlank()) {
            return ResponseEntity.status(400).body(Map.of(
                "message", "Nombre es requerido",
                "errors", Map.of("nombre", "Campo obligatorio")));
        }
        if (!nombre.equals(existente.getNombre()) && sensorService.existeNombre(nombre)) {
            return ResponseEntity.status(409).body(Map.of(
                "message", "Nombre duplicado",
                "errors", Map.of("nombre", "Ya existe un sensor con ese nombre")));
        }
        existente.setNombre(nombre);
        Sensor actualizado = sensorService.actualizar(existente);
        return ResponseEntity.ok(Map.of("id", actualizado.getId(), "nombre", actualizado.getNombre()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        Sensor existente = sensorService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body(Map.of(
                "message", "Sensor no encontrado",
                "errors", Map.of("id", "No existe sensor con ese id")));
        }
        sensorService.eliminar(id);
        return ResponseEntity.ok(Map.of(
            "message", "Sensor eliminado correctamente",
            "id", id));
    }

    private String generarIdDesdeNombre(String nombre) {
        String slug = nombre.toLowerCase()
            .replaceAll("[áàä]", "a")
            .replaceAll("[éèë]", "e")
            .replaceAll("[íìï]", "i")
            .replaceAll("[óòö]", "o")
            .replaceAll("[úùü]", "u")
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
        String uuid = java.util.UUID.randomUUID().toString().substring(0,8);
        return slug + "-" + uuid;
    }
}
