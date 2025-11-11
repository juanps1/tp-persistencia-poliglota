package com.tp.persistencia.persistencia_poliglota.repository.spec;

import com.tp.persistencia.persistencia_poliglota.model.sql.Alerta;
import com.tp.persistencia.persistencia_poliglota.model.sql.EstadoAlerta;
import com.tp.persistencia.persistencia_poliglota.model.sql.TipoAlerta;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class AlertaSpecifications {

    public static Specification<Alerta> tipo(TipoAlerta tipo) {
        return (root, query, cb) -> tipo == null ? null : cb.equal(root.get("tipo"), tipo);
    }

    public static Specification<Alerta> estado(EstadoAlerta estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Alerta> sensorId(String sensorId) {
        return (root, query, cb) -> (sensorId == null || sensorId.isBlank()) ? null : cb.equal(root.get("sensorId"), sensorId);
    }

    public static Specification<Alerta> rango(Instant desde, Instant hasta) {
        return (root, query, cb) -> {
            if (desde == null && hasta == null) return null;
            if (desde != null && hasta != null) return cb.between(root.get("fechaHora"), desde, hasta);
            if (desde != null) return cb.greaterThanOrEqualTo(root.get("fechaHora"), desde);
            return cb.lessThan(root.get("fechaHora"), hasta);
        };
    }

    public static Specification<Alerta> search(String text) {
        return (root, query, cb) -> (text == null || text.isBlank()) ? null : cb.like(cb.lower(root.get("descripcion")), "%" + text.toLowerCase() + "%");
    }
}