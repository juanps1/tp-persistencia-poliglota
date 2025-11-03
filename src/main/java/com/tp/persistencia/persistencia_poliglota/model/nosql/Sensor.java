package com.tp.persistencia.persistencia_poliglota.model.nosql;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "sensores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

    @Id
    private String id;
    private String nombre;
    private String tipo; // temperatura o humedad
    private double latitud;
    private double longitud;
    private String ciudad;
    private String pais;
    private String estado; // activo, inactivo, falla
    private LocalDateTime fechaInicioEmision;
}
