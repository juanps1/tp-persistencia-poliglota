package com.tp.persistencia.persistencia_poliglota.service;

import com.tp.persistencia.persistencia_poliglota.model.sql.CuentaCorriente;
import com.tp.persistencia.persistencia_poliglota.model.sql.MovimientoCuenta;
import com.tp.persistencia.persistencia_poliglota.repository.CuentaCorrienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public Optional<CuentaCorriente> obtenerPorId(Long id) {
        return cuentaCorrienteRepository.findById(id);
    }

    public Optional<CuentaCorriente> obtenerPorUsuarioId(Long usuarioId) {
        return cuentaCorrienteRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Registra un débito (resta) en la cuenta corriente
     */
    @Transactional
    public CuentaCorriente registrarDebito(Long cuentaId, Double monto, String concepto, Long facturaId) {
        CuentaCorriente cuenta = cuentaCorrienteRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta corriente no encontrada"));

        MovimientoCuenta movimiento = new MovimientoCuenta();
        movimiento.setTipo("debito");
        movimiento.setMonto(monto);
        movimiento.setConcepto(concepto);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setFacturaId(facturaId);

        cuenta.getMovimientos().add(movimiento);
        cuenta.setSaldoActual(cuenta.getSaldoActual() - monto);

        return cuentaCorrienteRepository.save(cuenta);
    }

    /**
     * Registra un crédito (suma) en la cuenta corriente
     */
    @Transactional
    public CuentaCorriente registrarCredito(Long cuentaId, Double monto, String concepto, Long facturaId) {
        CuentaCorriente cuenta = cuentaCorrienteRepository.findById(cuentaId)
                .orElseThrow(() -> new RuntimeException("Cuenta corriente no encontrada"));

        MovimientoCuenta movimiento = new MovimientoCuenta();
        movimiento.setTipo("credito");
        movimiento.setMonto(monto);
        movimiento.setConcepto(concepto);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setFacturaId(facturaId);

        cuenta.getMovimientos().add(movimiento);
        cuenta.setSaldoActual(cuenta.getSaldoActual() + monto);

        return cuentaCorrienteRepository.save(cuenta);
    }
}
