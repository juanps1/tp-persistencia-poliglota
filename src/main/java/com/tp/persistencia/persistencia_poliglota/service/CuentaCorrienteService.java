package com.tp.persistencia.persistencia_poliglota.service;

import com.tp.persistencia.persistencia_poliglota.model.sql.CuentaCorriente;
import com.tp.persistencia.persistencia_poliglota.repository.CuentaCorrienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CuentaCorrienteService {

    private final CuentaCorrienteRepository cuentaCorrienteRepository;

    public CuentaCorrienteService(CuentaCorrienteRepository cuentaCorrienteRepository) {
        this.cuentaCorrienteRepository = cuentaCorrienteRepository;
    }

    public List<CuentaCorriente> listar() {
        return cuentaCorrienteRepository.findAll();
    }

    public CuentaCorriente guardar(CuentaCorriente cuentaCorriente) {
        return cuentaCorrienteRepository.save(cuentaCorriente);
    }
}
