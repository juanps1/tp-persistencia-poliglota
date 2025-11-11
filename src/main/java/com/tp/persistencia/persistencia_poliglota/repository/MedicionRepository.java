package com.tp.persistencia.persistencia_poliglota.repository;

import com.tp.persistencia.persistencia_poliglota.model.nosql.Medicion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicionRepository extends MongoRepository<Medicion, String> {
    List<Medicion> findBySensorId(String sensorId);
    
    // Buscar mediciones por sensor y rango de fechas
    List<Medicion> findBySensorIdAndFechaHoraBetween(String sensorId, LocalDateTime desde, LocalDateTime hasta);
    
    // Buscar mediciones por lista de sensores (para filtrar por ciudad/país)
    List<Medicion> findBySensorIdInAndFechaHoraBetween(List<String> sensorIds, LocalDateTime desde, LocalDateTime hasta);
    
    // Eliminar mediciones por sensor
    long deleteBySensorId(String sensorId);
}

