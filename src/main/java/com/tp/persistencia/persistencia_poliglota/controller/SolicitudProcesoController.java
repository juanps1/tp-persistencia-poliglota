package com.tp.persistencia.persistencia_poliglota.controller;


import com.tp.persistencia.persistencia_poliglota.model.sql.SolicitudProceso;
import com.tp.persistencia.persistencia_poliglota.service.SolicitudProcesoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public SolicitudProceso guardar(@RequestBody SolicitudProceso solicitud) {
        return solicitudProcesoService.guardar(solicitud);
    }
}
