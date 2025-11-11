package com.tp.persistencia.persistencia_poliglota.model.sql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "alertas", indexes = {
        @Index(name = "idx_alerta_estado_fecha", columnList = "estado, fecha_hora"),
        @Index(name = "idx_alerta_tipo_sensor", columnList = "tipo, sensor_id")
})
@Getter
@Setter
public class Alerta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoAlerta tipo;

    @Column(name = "sensor_id", length = 100)
    private String sensorId; // null cuando climática

    @Column(name = "fecha_hora", nullable = false)
    private Instant fechaHora = Instant.now();

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoAlerta estado = EstadoAlerta.ACTIVA;

    @Enumerated(EnumType.STRING)
    @Column(length = 15)
    private Severidad severidad; // opcional

    @Column(length = 40)
    private String origen; // umbral | externo | manual

    private Instant resueltaEn;
    private Long resueltaPor;
}