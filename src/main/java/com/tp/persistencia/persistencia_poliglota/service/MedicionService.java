package com.tp.persistencia.persistencia_poliglota.service;

import com.tp.persistencia.persistencia_poliglota.model.nosql.Medicion;
import com.tp.persistencia.persistencia_poliglota.repository.MedicionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicionService {
    private final MedicionRepository medicionRepository;

    public MedicionService(MedicionRepository medicionRepository) {
        this.medicionRepository = medicionRepository;
    }

    public List<Medicion> listarTodas() {
        return medicionRepository.findAll();
    }

    public List<Medicion> listarPorSensor(String sensorId) {
        return medicionRepository.findBySensorId(sensorId);
    }

    public Medicion guardarMedicion(Medicion medicion) {
        return medicionRepository.save(medicion);
    }

    public Medicion buscarPorId(String id) {
        return medicionRepository.findById(id).orElse(null);
    }

    public void eliminar(String id) {
        medicionRepository.deleteById(id);
    }

    public long eliminarPorSensor(String sensorId) {
        return medicionRepository.deleteBySensorId(sensorId);
    }
}

