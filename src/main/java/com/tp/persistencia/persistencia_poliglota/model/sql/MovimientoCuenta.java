package com.tp.persistencia.persistencia_poliglota.model.sql;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_cuenta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCuenta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String tipo; // debito / credito
    
    @Column(nullable = false)
    private Double monto;
    
    @Column(nullable = false)
    private String concepto; // "Factura #123", "Pago recibido", "Carga de saldo"
    
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
    
    @Column
    private Long facturaId; // Si el movimiento está relacionado con una factura
}
