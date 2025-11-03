package com.tp.persistencia.persistencia_poliglota.model.sql;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "cuentas_corrientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CuentaCorriente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    private double saldoActual;

    @ElementCollection
    private List<String> historialMovimientos;
}
