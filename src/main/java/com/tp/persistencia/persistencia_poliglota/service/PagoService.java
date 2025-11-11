package com.tp.persistencia.persistencia_poliglota.service;
import com.tp.persistencia.persistencia_poliglota.model.sql.CuentaCorriente;
import com.tp.persistencia.persistencia_poliglota.model.sql.Factura;

import com.tp.persistencia.persistencia_poliglota.model.sql.Pago;
import com.tp.persistencia.persistencia_poliglota.repository.CuentaCorrienteRepository;
import com.tp.persistencia.persistencia_poliglota.repository.FacturaRepository;
import com.tp.persistencia.persistencia_poliglota.repository.PagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final FacturaRepository facturaRepository;
    private final CuentaCorrienteRepository cuentaCorrienteRepository;
    private final CuentaCorrienteService cuentaCorrienteService;
    private final FacturaService facturaService;

    public PagoService(PagoRepository pagoRepository, 
                      FacturaRepository facturaRepository,
                      CuentaCorrienteRepository cuentaCorrienteRepository,
                      CuentaCorrienteService cuentaCorrienteService,
                      FacturaService facturaService) {
        this.pagoRepository = pagoRepository;
    this.facturaRepository = facturaRepository;
    this.cuentaCorrienteRepository = cuentaCorrienteRepository;
    this.cuentaCorrienteService = cuentaCorrienteService;
    this.facturaService = facturaService;
    }

    public List<Pago> listarTodos() {
        return pagoRepository.findAll();
    }

    public List<Pago> listarPorFactura(Long facturaId) {
        return pagoRepository.findByFacturaId(facturaId);
    }

    public Optional<Pago> obtenerPorId(Long id) {
        return pagoRepository.findById(id);
    }

    /**
     * Registra un pago para una factura y actualiza su estado y cuenta corriente si aplica
     */
    @Transactional
    public Pago registrarPago(Long facturaId, Double montoPagado, String metodoPago) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        if ("pagada".equals(factura.getEstado())) {
            throw new RuntimeException("La factura ya está pagada");
        }

        // Crear pago
        Pago pago = new Pago();
        pago.setFactura(factura);
        pago.setMontoPagado(montoPagado);
        pago.setMetodoPago(metodoPago);
        pago.setFechaPago(LocalDateTime.now());

        Pago pagoGuardado = pagoRepository.save(pago);

        // Si el monto pagado cubre el total de la factura, marcarla como pagada
        if (montoPagado >= factura.getMonto()) {
            facturaService.marcarComoPagada(facturaId);

            // Si el usuario tiene cuenta corriente, acreditar el pago
            Optional<CuentaCorriente> cuentaOpt = cuentaCorrienteRepository
                    .findByUsuarioId(factura.getUsuario().getId());
            
            if (cuentaOpt.isPresent()) {
                cuentaCorrienteService.registrarCredito(
                        cuentaOpt.get().getId(),
                        montoPagado,
                        "Pago de factura #" + facturaId,
                        facturaId
                );
            }
        }

        return pagoGuardado;
    }

    public void eliminar(Long id) {
        pagoRepository.deleteById(id);
    }
}
