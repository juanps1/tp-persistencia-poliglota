package com.tp.persistencia.persistencia_poliglota.model.sql;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo; // "privada" o "grupal"

    @Column
    private String nombre; // null para privadas, nombre del grupo para grupales

    @ManyToOne
    @JoinColumn(name = "creador_id")
    private Usuario creador;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column
    private LocalDateTime ultimaActividad = LocalDateTime.now();
}
