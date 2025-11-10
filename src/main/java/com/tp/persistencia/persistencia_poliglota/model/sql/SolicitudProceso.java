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

    @Column(nullable = false)
    private String tipoProceso; // informe_max_min, informe_promedio, alerta, consulta

    @Column(columnDefinition = "TEXT")
    private String parametrosJson; // Filtros del proceso en formato JSON

    @Column(nullable = false)
    private String estado; // pendiente, en_proceso, completado, error

    @Column(nullable = false)
    private LocalDateTime fechaSolicitud = LocalDateTime.now();

    @Column
    private LocalDateTime fechaFinalizacion;

    @Column(columnDefinition = "TEXT")
    private String resultadoJson; // Resultado procesado en formato JSON

    @Column
    private String rutaArchivoPdf; // Ruta del PDF generado

    @Column(columnDefinition = "TEXT")
    private String mensajeError; // Mensaje de error si falló
}
