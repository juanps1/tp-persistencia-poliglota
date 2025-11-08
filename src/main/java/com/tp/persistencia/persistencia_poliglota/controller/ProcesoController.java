package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Proceso;
import com.tp.persistencia.persistencia_poliglota.service.ProcesoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/procesos")
public class ProcesoController {

    private final ProcesoService procesoService;

    public ProcesoController(ProcesoService procesoService) {
        this.procesoService = procesoService;
    }

    @GetMapping
    public List<Proceso> listar() {
        return procesoService.listar();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Proceso proceso) {
        if (proceso.getNombre() == null || proceso.getNombre().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Nombre es requerido", "errors", Map.of("nombre", "Campo obligatorio")));
        }
        Proceso guardado = procesoService.guardar(proceso);
        return ResponseEntity.status(201).body(guardado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        boolean eliminado = procesoService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.status(204).build();
        } else {
            return ResponseEntity.status(404).body(
                Map.of("message", "Proceso no encontrado", "errors", Map.of("id", "No existe proceso con ese id")));
        }
    }
}
