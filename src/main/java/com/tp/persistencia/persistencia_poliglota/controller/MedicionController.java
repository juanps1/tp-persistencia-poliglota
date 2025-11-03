package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.nosql.Medicion;
import com.tp.persistencia.persistencia_poliglota.service.MedicionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Medicion guardar(@RequestBody Medicion medicion) {
        return medicionService.guardarMedicion(medicion);
    }
}
