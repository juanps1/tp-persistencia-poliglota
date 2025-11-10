package com.tp.persistencia.persistencia_poliglota.model.sql;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversacion_id", nullable = false)
    private Conversacion conversacion;

    @ManyToOne
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;

    @Column(nullable = false)
    private LocalDateTime fechaEnvio = LocalDateTime.now();

    @Column
    private LocalDateTime fechaEdicion; // si se editó el mensaje

    @Column(nullable = false)
    private Boolean editado = false;

    @Column(nullable = false)
    private Boolean eliminado = false; // borrado lógico
}
