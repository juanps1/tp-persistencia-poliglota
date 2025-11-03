package com.tp.persistencia.persistencia_poliglota.repository;

import com.tp.persistencia.persistencia_poliglota.model.nosql.Medicion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MedicionRepository extends MongoRepository<Medicion, String> {
    List<Medicion> findBySensorId(String sensorId);
}
