package com.tp.persistencia.persistencia_poliglota.model.sql;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "procesos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private String tipo;
    private double costo;
}
