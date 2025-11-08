package com.tp.persistencia.persistencia_poliglota.controller;


import com.tp.persistencia.persistencia_poliglota.model.sql.SolicitudProceso;
import com.tp.persistencia.persistencia_poliglota.service.SolicitudProcesoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudProcesoController {

    private final SolicitudProcesoService solicitudProcesoService;

    public SolicitudProcesoController(SolicitudProcesoService solicitudProcesoService) {
        this.solicitudProcesoService = solicitudProcesoService;
    }

    @GetMapping
    public List<SolicitudProceso> listar() {
        return solicitudProcesoService.listar();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody SolicitudProceso solicitud) {
        if (solicitud.getUsuario() == null || solicitud.getUsuario().getId() == null) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Usuario es requerido", "errors", Map.of("usuario", "Debe enviar objeto {id}")));
        }
        if (solicitud.getEstado() == null || solicitud.getEstado().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Estado es requerido", "errors", Map.of("estado", "Campo obligatorio")));
        }
        SolicitudProceso guardada = solicitudProcesoService.guardar(solicitud);
        return ResponseEntity.status(201).body(guardada);
    }
}
