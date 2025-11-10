package com.tp.persistencia.persistencia_poliglota.model.sql;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participantes_conversacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteConversacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversacion_id", nullable = false)
    private Conversacion conversacion;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaIngreso = LocalDateTime.now();

    @Column
    private LocalDateTime ultimaLectura; // timestamp del último mensaje leído por este usuario

    @Column(nullable = false)
    private Boolean activo = true; // si el usuario está activo en la conversación
}
