package com.tp.persistencia.persistencia_poliglota.service;


import com.tp.persistencia.persistencia_poliglota.model.sql.Proceso;
import com.tp.persistencia.persistencia_poliglota.repository.ProcesoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProcesoService {

    private final ProcesoRepository procesoRepository;

    public ProcesoService(ProcesoRepository procesoRepository) {
        this.procesoRepository = procesoRepository;
    }

    public List<Proceso> listar() {
        return procesoRepository.findAll();
    }

    public Proceso guardar(Proceso proceso) {
        return procesoRepository.save(proceso);
    }

    public boolean eliminar(Long id) {
        if (procesoRepository.existsById(id)) {
            procesoRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}
