package com.tp.persistencia.persistencia_poliglota.model.nosql;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "alertas")
public class Alerta {

    @Id
    private String id;

    private Long usuarioId;       // ID del usuario SQL
    private String mensaje;
    private String nivel;         // por ejemplo: "ALTA", "MEDIA", "BAJA"
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
