package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.CuentaCorriente;
import com.tp.persistencia.persistencia_poliglota.service.CuentaCorrienteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import java.util.Map;

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
    public ResponseEntity<?> guardar(@RequestBody CuentaCorriente cuentaCorriente) {
        if (cuentaCorriente.getUsuario() == null || cuentaCorriente.getUsuario().getId() == null) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Usuario es requerido", "errors", Map.of("usuario", "Debe enviar objeto {id}")));
        }
        // saldoActual es double, no puede ser null, pero validamos que esté presente (puede ser 0)
        // Si quieres forzar saldo positivo, cambia la validación aquí
        CuentaCorriente guardada = cuentaCorrienteService.guardar(cuentaCorriente);
        return ResponseEntity.status(201).body(guardada);
    }
}
