package com.tp.persistencia.persistencia_poliglota.model.sql;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fechaEmision = LocalDateTime.now();

    @Column
    private LocalDateTime fechaVencimiento;

    @Column(nullable = false)
    private Double monto = 0.0;

    @Column(nullable = false)
    private String estado = "pendiente"; // pendiente / pagada / vencida

    @ElementCollection
    @CollectionTable(name = "factura_solicitudes", joinColumns = @JoinColumn(name = "factura_id"))
    @Column(name = "solicitud_proceso_id")
    private List<Long> solicitudesProcesoIds; // IDs de SolicitudProceso facturadas

    @Column(columnDefinition = "TEXT")
    private String observaciones;
}
