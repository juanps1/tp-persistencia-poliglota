package com.tp.persistencia.persistencia_poliglota.service;



import com.tp.persistencia.persistencia_poliglota.model.nosql.Sensor;
import com.tp.persistencia.persistencia_poliglota.repository.SensorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorService {
    private final SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public List<Sensor> listarSensores() {
        return sensorRepository.findAll();
    }

    public Sensor guardarSensor(Sensor sensor) {
        return sensorRepository.save(sensor);
    }

    public boolean existeNombre(String nombre) { return sensorRepository.existsByNombre(nombre); }

    public Sensor buscarPorId(String id) { return sensorRepository.findById(id).orElse(null); }

    public Sensor actualizar(Sensor sensor) { return sensorRepository.save(sensor); }

    public void eliminar(String id) { 
        sensorRepository.deleteById(id); 
    }
}

