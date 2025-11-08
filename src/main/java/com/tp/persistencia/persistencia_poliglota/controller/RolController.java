package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Rol;
import com.tp.persistencia.persistencia_poliglota.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/roles")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @GetMapping
    public List<Rol> getAll() {
        return rolRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Rol rol) {
        if (rol.getDescripcion() == null || rol.getDescripcion().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Descripcion es requerida", "errors", Map.of("descripcion", "Campo obligatorio")));
        }
        // Verificar duplicado
        if (rolRepository.findByDescripcion(rol.getDescripcion()) != null) {
            return ResponseEntity.status(409).body(
                Map.of("message", "Rol duplicado", "errors", Map.of("descripcion", "Ya existe un rol con esa descripcion")));
        }
        Rol guardado = rolRepository.save(rol);
        return ResponseEntity.status(201).body(guardado);
    }
}
