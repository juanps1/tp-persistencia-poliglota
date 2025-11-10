package com.tp.persistencia.persistencia_poliglota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.persistencia.persistencia_poliglota.model.sql.SolicitudProceso;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import com.tp.persistencia.persistencia_poliglota.service.SolicitudProcesoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/solicitudes-proceso")
public class SolicitudProcesoController {

    private final SolicitudProcesoService solicitudProcesoService;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    public SolicitudProcesoController(SolicitudProcesoService solicitudProcesoService,
                                      UsuarioRepository usuarioRepository) {
        this.solicitudProcesoService = solicitudProcesoService;
        this.usuarioRepository = usuarioRepository;
        this.objectMapper = new ObjectMapper();
    }

    // Listar todas las solicitudes
    @GetMapping
    public ResponseEntity<?> listar() {
        List<SolicitudProceso> solicitudes = solicitudProcesoService.listar();
        return ResponseEntity.ok(solicitudes);
    }

    // Listar solicitudes de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<SolicitudProceso> solicitudes = solicitudProcesoService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al obtener solicitudes", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Obtener detalle de una solicitud
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<SolicitudProceso> solicitud = solicitudProcesoService.buscarPorId(id);
        
        if (solicitud.isEmpty()) {
            return ResponseEntity.status(404).body(
                Map.of("message", "Solicitud no encontrada", 
                       "errors", Map.of("id", "No existe")));
        }

        return ResponseEntity.ok(solicitud.get());
    }

    // Crear nueva solicitud de proceso
    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody Map<String, Object> body) {
        try {
            // Validaciones
            if (!body.containsKey("usuarioId") || !body.containsKey("tipoProceso") 
                || !body.containsKey("parametros")) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Faltan campos requeridos", 
                           "errors", Map.of("usuarioId", "Requerido", 
                                           "tipoProceso", "Requerido",
                                           "parametros", "Requerido")));
            }

            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            String tipoProceso = body.get("tipoProceso").toString();
            
            // Validar tipo de proceso
            if (!tipoProceso.matches("informe_max_min|informe_promedio|alerta|consulta")) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Tipo de proceso inválido", 
                           "errors", Map.of("tipoProceso", 
                               "Debe ser: informe_max_min, informe_promedio, alerta o consulta")));
            }

            // Buscar usuario
            Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
            if (usuario == null) {
                return ResponseEntity.status(404).body(
                    Map.of("message", "Usuario no encontrado", 
                           "errors", Map.of("usuarioId", "No existe")));
            }

            // Crear solicitud
            SolicitudProceso solicitud = new SolicitudProceso();
            solicitud.setUsuario(usuario);
            solicitud.setTipoProceso(tipoProceso);
            solicitud.setParametrosJson(objectMapper.writeValueAsString(body.get("parametros")));
            solicitud.setEstado("pendiente");
            solicitud.setFechaSolicitud(LocalDateTime.now());

            SolicitudProceso guardada = solicitudProcesoService.guardar(solicitud);
            
            return ResponseEntity.status(201).body(guardada);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al crear solicitud", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Obtener resultado JSON de una solicitud
    @GetMapping("/{id}/resultado")
    public ResponseEntity<?> obtenerResultado(@PathVariable Long id) {
        try {
            Optional<SolicitudProceso> solicitudOpt = solicitudProcesoService.buscarPorId(id);
            
            if (solicitudOpt.isEmpty()) {
                return ResponseEntity.status(404).body(
                    Map.of("message", "Solicitud no encontrada", 
                           "errors", Map.of("id", "No existe")));
            }

            SolicitudProceso solicitud = solicitudOpt.get();

            if (!solicitud.getEstado().equals("completado")) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Solicitud no completada", 
                           "errors", Map.of("estado", "Estado actual: " + solicitud.getEstado())));
            }

            if (solicitud.getResultadoJson() == null) {
                return ResponseEntity.status(404).body(
                    Map.of("message", "Resultado no disponible", 
                           "errors", Map.of("resultado", "No se generó resultado")));
            }

            // Parsear JSON y retornar
            Map<String, Object> resultado = objectMapper.readValue(
                solicitud.getResultadoJson(), 
                Map.class);

            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al obtener resultado", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Descargar PDF de una solicitud
    @GetMapping("/{id}/descargar-pdf")
    public ResponseEntity<?> descargarPdf(@PathVariable Long id) {
        try {
            Optional<SolicitudProceso> solicitudOpt = solicitudProcesoService.buscarPorId(id);
            
            if (solicitudOpt.isEmpty()) {
                return ResponseEntity.status(404).body(
                    Map.of("message", "Solicitud no encontrada"));
            }

            SolicitudProceso solicitud = solicitudOpt.get();

            if (!solicitud.getEstado().equals("completado")) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Solicitud no completada"));
            }

            if (solicitud.getRutaArchivoPdf() == null) {
                return ResponseEntity.status(404).body(
                    Map.of("message", "PDF no disponible"));
            }

            File archivoPdf = new File(solicitud.getRutaArchivoPdf());
            
            if (!archivoPdf.exists()) {
                return ResponseEntity.status(404).body(
                    Map.of("message", "Archivo PDF no encontrado"));
            }

            Resource resource = new FileSystemResource(archivoPdf);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + archivoPdf.getName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al descargar PDF", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Eliminar solicitud
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            boolean eliminado = solicitudProcesoService.eliminar(id);
            
            if (!eliminado) {
                return ResponseEntity.status(404).body(
                    Map.of("message", "Solicitud no encontrada", 
                           "errors", Map.of("id", "No existe")));
            }

            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al eliminar solicitud", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Re-ejecutar una solicitud fallida
    @PostMapping("/{id}/reejecutar")
    public ResponseEntity<?> reejecutar(@PathVariable Long id) {
        try {
            Optional<SolicitudProceso> solicitudOpt = solicitudProcesoService.buscarPorId(id);
            
            if (solicitudOpt.isEmpty()) {
                return ResponseEntity.status(404).body(
                    Map.of("message", "Solicitud no encontrada"));
            }

            SolicitudProceso solicitud = solicitudOpt.get();

            // Resetear estado a pendiente
            solicitud.setEstado("pendiente");
            solicitud.setMensajeError(null);
            solicitud.setResultadoJson(null);
            solicitud.setRutaArchivoPdf(null);
            solicitudProcesoService.guardar(solicitud);

            return ResponseEntity.ok(Map.of("message", "Solicitud reencolada para ejecución"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al reejecutar solicitud", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }
}
