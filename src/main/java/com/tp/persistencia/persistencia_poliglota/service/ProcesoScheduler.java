package com.tp.persistencia.persistencia_poliglota.service;

import com.tp.persistencia.persistencia_poliglota.model.sql.SolicitudProceso;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcesoScheduler {

    private final SolicitudProcesoService solicitudProcesoService;

    public ProcesoScheduler(SolicitudProcesoService solicitudProcesoService) {
        this.solicitudProcesoService = solicitudProcesoService;
    }

    // Ejecuta cada 60 segundos (1 minuto)
    @Scheduled(fixedDelay = 60000)
    public void procesarSolicitudesPendientes() {
        List<SolicitudProceso> pendientes = solicitudProcesoService.buscarPendientes();
        
        for (SolicitudProceso solicitud : pendientes) {
            try {
                solicitudProcesoService.ejecutarProceso(solicitud);
            } catch (Exception e) {
                System.err.println("Error al procesar solicitud " + solicitud.getId() + ": " + e.getMessage());
            }
        }
    }
}
