package com.tp.persistencia.persistencia_poliglota.model.nosql;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "mediciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicion {

    @Id
    private String id;
    private String sensorId;
    private LocalDateTime fechaHora;
    private double temperatura;
    private double humedad;
}
