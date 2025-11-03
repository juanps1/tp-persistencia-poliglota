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

    private LocalDateTime fechaEmision = LocalDateTime.now();
    private String estado; // pendiente / pagada / vencida

    @OneToMany
    private List<Proceso> procesosFacturados;
}
