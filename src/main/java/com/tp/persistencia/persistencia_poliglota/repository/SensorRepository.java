package com.tp.persistencia.persistencia_poliglota.repository;

import com.tp.persistencia.persistencia_poliglota.model.nosql.Sensor;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SensorRepository extends MongoRepository<Sensor, String> {
	boolean existsByNombre(String nombre);
	
	// Buscar sensores por ubicación
	List<Sensor> findByCiudad(String ciudad);
	List<Sensor> findByPais(String pais);
	List<Sensor> findByCiudadAndPais(String ciudad, String pais);
}
 
