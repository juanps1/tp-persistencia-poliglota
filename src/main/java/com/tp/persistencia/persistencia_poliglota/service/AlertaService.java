package com.tp.persistencia.persistencia_poliglota.service;

import com.tp.persistencia.persistencia_poliglota.model.sql.*;
import com.tp.persistencia.persistencia_poliglota.repository.AlertaRepository;
import com.tp.persistencia.persistencia_poliglota.repository.spec.AlertaSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;

    public AlertaService(AlertaRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    public Page<Alerta> listar(String tipo, String estado, String sensorId, Instant desde, Instant hasta, String search, Pageable pageable) {
    Specification<Alerta> spec = Specification.where(AlertaSpecifications.tipo(parseTipo(tipo)))
        .and(AlertaSpecifications.estado(parseEstado(estado)))
        .and(AlertaSpecifications.sensorId(sensorId))
        .and(AlertaSpecifications.rango(desde, hasta))
        .and(AlertaSpecifications.search(search));
        return alertaRepository.findAll(spec, pageable == null ? PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "fechaHora")) : pageable);
    }

    public Alerta detalle(Long id) {
    if (id == null) return null;
        return alertaRepository.findById(id).orElse(null);
    }

    public Alerta crear(TipoAlerta tipo, String sensorId, String descripcion, Severidad sev, String origen) {
        Alerta a = new Alerta();
        a.setTipo(tipo);
        a.setSensorId(sensorId);
        a.setDescripcion(descripcion);
        a.setSeveridad(sev);
        a.setOrigen(origen);
        a.setFechaHora(Instant.now());
        a.setEstado(EstadoAlerta.ACTIVA);
        alertaRepository.save(a);
        return a;
    }

    public Alerta resolver(Long id, Long usuarioId, String comentario) {
    if (id == null) return null;
    Optional<Alerta> opt = alertaRepository.findById(id);
        if (opt.isEmpty()) return null;
        Alerta a = opt.get();
        if (a.getEstado() == EstadoAlerta.ACTIVA) {
            a.setEstado(EstadoAlerta.RESUELTA);
            a.setResueltaEn(Instant.now());
            a.setResueltaPor(usuarioId);
            alertaRepository.save(a);
        }
        return a;
    }

    public Alerta reabrir(Long id, Long usuarioId, String motivo) {
    if (id == null) return null;
    Optional<Alerta> opt = alertaRepository.findById(id);
        if (opt.isEmpty()) return null;
        Alerta a = opt.get();
        if (a.getEstado() == EstadoAlerta.RESUELTA) {
            a.setEstado(EstadoAlerta.ACTIVA);
            a.setResueltaEn(null);
            a.setResueltaPor(null);
            alertaRepository.save(a);
        }
        return a;
    }

    public Map<String, Long> metrics() {
        long activas = alertaRepository.countByEstado(EstadoAlerta.ACTIVA);
        Instant hoyInicio = ZonedDateTime.now(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        long nuevasHoy = alertaRepository.countByRango(hoyInicio, Instant.now());
        Instant hace7Dias = Instant.now().minusSeconds(7 * 24 * 3600);
        long resueltasSemana = alertaRepository.countResueltasDesde(hace7Dias);
        long sensorActivas = alertaRepository.countByEstadoAndTipo(EstadoAlerta.ACTIVA, TipoAlerta.SENSOR);
        long climaticaActivas = alertaRepository.countByEstadoAndTipo(EstadoAlerta.ACTIVA, TipoAlerta.CLIMATICA);
        Map<String, Long> m = new HashMap<>();
        m.put("activas", activas);
        m.put("nuevasHoy", nuevasHoy);
        m.put("resueltasSemana", resueltasSemana);
        m.put("sensorActivas", sensorActivas);
        m.put("climaticaActivas", climaticaActivas);
        return m;
    }

    public boolean eliminar(Long id) {
    if (id == null) return false;
    if (!alertaRepository.existsById(id)) return false;
    alertaRepository.deleteById(id);
        return true;
    }

    private TipoAlerta parseTipo(String tipo) {
        if (tipo == null) return null;
        try { return TipoAlerta.valueOf(tipo.toUpperCase()); } catch (Exception e) { return null; }
    }
    private EstadoAlerta parseEstado(String estado) {
        if (estado == null) return null;
        try { return EstadoAlerta.valueOf(estado.toUpperCase()); } catch (Exception e) { return null; }
    }

    // (helper removed; no longer needed after DTO removal)
}