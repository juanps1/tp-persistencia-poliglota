package com.tp.persistencia.persistencia_poliglota.service;



import com.tp.persistencia.persistencia_poliglota.model.sql.SolicitudProceso;
import com.tp.persistencia.persistencia_poliglota.repository.SolicitudProcesoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SolicitudProcesoService {

    private final SolicitudProcesoRepository solicitudProcesoRepository;

    public SolicitudProcesoService(SolicitudProcesoRepository solicitudProcesoRepository) {
        this.solicitudProcesoRepository = solicitudProcesoRepository;
    }

    public List<SolicitudProceso> listar() {
        return solicitudProcesoRepository.findAll();
    }

    public SolicitudProceso guardar(SolicitudProceso solicitud) {
        return solicitudProcesoRepository.save(solicitud);
    }
}
