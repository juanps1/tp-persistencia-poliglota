package com.tp.persistencia.persistencia_poliglota.service;

import com.tp.persistencia.persistencia_poliglota.model.sql.*;
import com.tp.persistencia.persistencia_poliglota.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final SolicitudProcesoRepository solicitudProcesoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CuentaCorrienteRepository cuentaCorrienteRepository;
    private final CuentaCorrienteService cuentaCorrienteService;

    public FacturaService(FacturaRepository facturaRepository, 
                         SolicitudProcesoRepository solicitudProcesoRepository,
                         UsuarioRepository usuarioRepository,
                         CuentaCorrienteRepository cuentaCorrienteRepository,
                         CuentaCorrienteService cuentaCorrienteService) {
        this.facturaRepository = facturaRepository;
        this.solicitudProcesoRepository = solicitudProcesoRepository;
        this.usuarioRepository = usuarioRepository;
        this.cuentaCorrienteRepository = cuentaCorrienteRepository;
        this.cuentaCorrienteService = cuentaCorrienteService;
    }

    public List<Factura> listarTodas() {
        return facturaRepository.findAll();
    }

    public List<Factura> listarPorUsuario(Long usuarioId) {
        return facturaRepository.findByUsuarioId(usuarioId);
    }

    public Optional<Factura> obtenerPorId(Long id) {
        return facturaRepository.findById(id);
    }

    /**
     * Genera una factura automática para una solicitud de proceso completada
     */
    @Transactional
    public Factura generarFacturaParaSolicitud(SolicitudProceso solicitud) {
        Usuario usuario = solicitud.getUsuario();
        
        if (solicitud.getProceso() == null || solicitud.getProceso().getCosto() <= 0) {
            throw new RuntimeException("La solicitud no tiene un proceso con costo asociado");
        }

        double monto = solicitud.getProceso().getCosto();

        // Crear factura individual
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setFechaVencimiento(LocalDateTime.now().plusDays(15)); // 15 días para pagar
        factura.setMonto(monto);
        factura.setEstado("pendiente");
        
        List<Long> solicitudesIds = new ArrayList<>();
        solicitudesIds.add(solicitud.getId());
        factura.setSolicitudesProcesoIds(solicitudesIds);
        factura.setObservaciones("Factura por proceso: " + solicitud.getTipoProceso());

        Factura facturaGuardada = facturaRepository.save(factura);

        // Si el usuario tiene cuenta corriente, debitar automáticamente
        Optional<CuentaCorriente> cuentaOpt = cuentaCorrienteRepository.findByUsuarioId(usuario.getId());
        if (cuentaOpt.isPresent()) {
            CuentaCorriente cuenta = cuentaOpt.get();
            cuentaCorrienteService.registrarDebito(
                    cuenta.getId(), 
                    monto, 
                    "Factura #" + facturaGuardada.getId(),
                    facturaGuardada.getId()
            );
        }

        return facturaGuardada;
    }

    /**
     * Genera una factura para un usuario basada en sus solicitudes completadas sin facturar (método manual)
     */
    @Transactional
    public Factura generarFacturaParaUsuario(Long usuarioId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar solicitudes completadas en el rango de fechas que no estén facturadas
        List<SolicitudProceso> solicitudesCompletadas = solicitudProcesoRepository
                .findByUsuarioIdAndEstadoAndFechaFinalizacionBetween(
                        usuarioId, 
                        "completado", 
                        fechaInicio, 
                        fechaFin
                );

        if (solicitudesCompletadas.isEmpty()) {
            throw new RuntimeException("No hay solicitudes completadas para facturar en el período especificado");
        }

        // Calcular monto total sumando el costo de cada proceso
        double montoTotal = solicitudesCompletadas.stream()
                .filter(s -> s.getProceso() != null)
                .mapToDouble(s -> s.getProceso().getCosto())
                .sum();

        // Crear factura
        Factura factura = new Factura();
        factura.setUsuario(usuario);
        factura.setFechaEmision(LocalDateTime.now());
        factura.setFechaVencimiento(LocalDateTime.now().plusDays(15)); // 15 días para pagar
        factura.setMonto(montoTotal);
        factura.setEstado("pendiente");
        
        List<Long> solicitudesIds = new ArrayList<>();
        for (SolicitudProceso s : solicitudesCompletadas) {
            solicitudesIds.add(s.getId());
        }
        factura.setSolicitudesProcesoIds(solicitudesIds);

        Factura facturaGuardada = facturaRepository.save(factura);

        // Si el usuario tiene cuenta corriente, debitar automáticamente
        Optional<CuentaCorriente> cuentaOpt = cuentaCorrienteRepository.findByUsuarioId(usuarioId);
        if (cuentaOpt.isPresent()) {
            CuentaCorriente cuenta = cuentaOpt.get();
            cuentaCorrienteService.registrarDebito(
                    cuenta.getId(), 
                    montoTotal, 
                    "Factura #" + facturaGuardada.getId(),
                    facturaGuardada.getId()
            );
        }

        return facturaGuardada;
    }

    /**
     * Marca una factura como pagada
     */
    @Transactional
    public Factura marcarComoPagada(Long facturaId) {
        Factura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        factura.setEstado("pagada");
        return facturaRepository.save(factura);
    }

    /**
     * Verifica y marca como vencidas las facturas que superaron su fecha de vencimiento
     */
    @Transactional
    public void actualizarFacturasVencidas() {
        List<Factura> facturasPendientes = facturaRepository.findByEstado("pendiente");
        LocalDateTime ahora = LocalDateTime.now();

        for (Factura factura : facturasPendientes) {
            if (factura.getFechaVencimiento() != null && factura.getFechaVencimiento().isBefore(ahora)) {
                factura.setEstado("vencida");
                facturaRepository.save(factura);
            }
        }
    }

    /**
     * Verifica si un usuario tiene facturas vencidas
     */
    public boolean tieneFacturasVencidas(Long usuarioId) {
        List<Factura> facturasVencidas = facturaRepository.findByUsuarioIdAndEstado(usuarioId, "vencida");
        return !facturasVencidas.isEmpty();
    }

    public Factura guardar(Factura factura) {
        return facturaRepository.save(factura);
    }

    public void eliminar(Long id) {
        facturaRepository.deleteById(id);
    }
}
