package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Proceso;
import com.tp.persistencia.persistencia_poliglota.service.ProcesoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public Proceso guardar(@RequestBody Proceso proceso) {
        return procesoService.guardar(proceso);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        procesoService.eliminar(id);
    }
}
