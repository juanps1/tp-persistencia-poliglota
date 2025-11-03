package com.tp.persistencia.persistencia_poliglota.model.nosql;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "alertas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alerta {

    @Id
    private String id;
    private String tipo; // sensor o climática
    private String sensorId;
    private LocalDateTime fechaHora;
    private String descripcion;
    private String estado; // activa o resuelta
}
