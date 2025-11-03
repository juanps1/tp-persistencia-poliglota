package com.tp.persistencia.persistencia_poliglota.repository;
import com.tp.persistencia.persistencia_poliglota.repository.AlertaRepository;
import com.tp.persistencia.persistencia_poliglota.model.nosql.Alerta;


import com.tp.persistencia.persistencia_poliglota.model.nosql.Alerta;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;


public interface AlertaRepository extends MongoRepository<Alerta, String> {
    List<Alerta> findByUsuarioId(Long usuarioId);
}
