package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.CuentaCorriente;
import com.tp.persistencia.persistencia_poliglota.service.CuentaCorrienteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas-corrientes")
public class CuentaCorrienteController {

    private final CuentaCorrienteService cuentaCorrienteService;

    public CuentaCorrienteController(CuentaCorrienteService cuentaCorrienteService) {
        this.cuentaCorrienteService = cuentaCorrienteService;
    }

    @GetMapping
    public List<CuentaCorriente> listar() {
        return cuentaCorrienteService.listar();
    }

    @PostMapping
    public CuentaCorriente guardar(@RequestBody CuentaCorriente cuentaCorriente) {
        return cuentaCorrienteService.guardar(cuentaCorriente);
    }
}
