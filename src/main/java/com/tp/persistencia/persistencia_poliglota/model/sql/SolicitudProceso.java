package com.tp.persistencia.persistencia_poliglota.model.sql;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_proceso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudProceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "proceso_id")
    private Proceso proceso;

    private LocalDateTime fechaSolicitud = LocalDateTime.now();
    private String estado; // pendiente / completado
}
