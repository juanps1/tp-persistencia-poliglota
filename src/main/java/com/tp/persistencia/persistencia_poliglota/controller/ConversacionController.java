package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Conversacion;
import com.tp.persistencia.persistencia_poliglota.model.sql.ParticipanteConversacion;
import com.tp.persistencia.persistencia_poliglota.service.ConversacionService;
import com.tp.persistencia.persistencia_poliglota.service.MensajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversaciones")
public class ConversacionController {

    private final ConversacionService conversacionService;
    private final MensajeService mensajeService;

    public ConversacionController(ConversacionService conversacionService, 
                                  MensajeService mensajeService) {
        this.conversacionService = conversacionService;
        this.mensajeService = mensajeService;
    }

    // Obtener todas las conversaciones de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> obtenerConversacionesUsuario(@PathVariable Long usuarioId) {
        try {
            List<Conversacion> conversaciones = conversacionService.obtenerConversacionesUsuario(usuarioId);
            
            // Enriquecer con datos adicionales
            List<Map<String, Object>> resultado = conversaciones.stream().map(conv -> {
                Map<String, Object> data = new HashMap<>();
                data.put("id", conv.getId());
                data.put("tipo", conv.getTipo());
                data.put("nombre", conv.getNombre());
                data.put("creador", conv.getCreador() != null ? 
                    Map.of("id", conv.getCreador().getId(), 
                           "nombreCompleto", conv.getCreador().getNombreCompleto()) : null);
                data.put("fechaCreacion", conv.getFechaCreacion());
                data.put("ultimaActividad", conv.getUltimaActividad());
                
                // Participantes
                List<ParticipanteConversacion> participantes = 
                    conversacionService.obtenerParticipantes(conv.getId());
                data.put("participantes", participantes.stream().map(p -> 
                    Map.of("id", p.getUsuario().getId(),
                           "nombreCompleto", p.getUsuario().getNombreCompleto(),
                           "email", p.getUsuario().getEmail())
                ).collect(Collectors.toList()));
                
                // Mensajes no leídos
                Long noLeidos = mensajeService.contarMensajesNoLeidos(conv.getId(), usuarioId);
                data.put("mensajesNoLeidos", noLeidos);
                
                return data;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al obtener conversaciones", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Crear conversación privada
    @PostMapping("/privada")
    public ResponseEntity<?> crearConversacionPrivada(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("usuario1Id") || !body.containsKey("usuario2Id")) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Faltan datos requeridos", 
                       "errors", Map.of("usuario1Id", "Requerido", "usuario2Id", "Requerido")));
        }

        try {
            Long usuario1Id = Long.valueOf(body.get("usuario1Id").toString());
            Long usuario2Id = Long.valueOf(body.get("usuario2Id").toString());

            if (usuario1Id.equals(usuario2Id)) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "No puedes crear conversación contigo mismo", 
                           "errors", Map.of("usuario2Id", "Debe ser diferente a usuario1Id")));
            }

            Conversacion conversacion = conversacionService.crearConversacionPrivada(usuario1Id, usuario2Id);
            
            if (conversacion == null) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Error al crear conversación", 
                           "errors", Map.of("general", "Usuarios no encontrados")));
            }

            return ResponseEntity.status(201).body(conversacion);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al crear conversación", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Crear conversación grupal
    @PostMapping("/grupal")
    public ResponseEntity<?> crearConversacionGrupal(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("nombre") || !body.containsKey("creadorId")) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Faltan datos requeridos", 
                       "errors", Map.of("nombre", "Requerido", "creadorId", "Requerido")));
        }

        try {
            String nombre = body.get("nombre").toString();
            Long creadorId = Long.valueOf(body.get("creadorId").toString());
            
            @SuppressWarnings("unchecked")
            List<Integer> participantesIdsInt = (List<Integer>) body.getOrDefault("participantesIds", new ArrayList<>());
            List<Long> participantesIds = participantesIdsInt.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

            if (nombre.trim().isEmpty()) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Nombre inválido", 
                           "errors", Map.of("nombre", "No puede estar vacío")));
            }

            Conversacion conversacion = conversacionService.crearConversacionGrupal(nombre, creadorId, participantesIds);
            
            if (conversacion == null) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Error al crear conversación grupal", 
                           "errors", Map.of("creadorId", "Creador no encontrado")));
            }

            return ResponseEntity.status(201).body(conversacion);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al crear conversación grupal", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Obtener detalles de una conversación
    @GetMapping("/{conversacionId}")
    public ResponseEntity<?> obtenerConversacion(@PathVariable Long conversacionId) {
        Optional<Conversacion> conversacion = conversacionService.obtenerConversacionPorId(conversacionId);
        
        if (conversacion.isEmpty()) {
            return ResponseEntity.status(404).body(
                Map.of("message", "Conversación no encontrada", 
                       "errors", Map.of("conversacionId", "No existe")));
        }

        return ResponseEntity.ok(conversacion.get());
    }

    // Obtener participantes de una conversación
    @GetMapping("/{conversacionId}/participantes")
    public ResponseEntity<?> obtenerParticipantes(@PathVariable Long conversacionId) {
        List<ParticipanteConversacion> participantes = 
            conversacionService.obtenerParticipantes(conversacionId);
        
        List<Map<String, Object>> resultado = participantes.stream().map(p -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id", p.getId());
            data.put("usuario", Map.of(
                "id", p.getUsuario().getId(),
                "nombreCompleto", p.getUsuario().getNombreCompleto(),
                "email", p.getUsuario().getEmail()
            ));
            data.put("fechaIngreso", p.getFechaIngreso());
            data.put("ultimaLectura", p.getUltimaLectura());
            data.put("activo", p.getActivo());
            return data;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(resultado);
    }

    // Agregar participante a conversación grupal
    @PostMapping("/{conversacionId}/participantes")
    public ResponseEntity<?> agregarParticipante(@PathVariable Long conversacionId,
                                                  @RequestBody Map<String, Object> body) {
        if (!body.containsKey("usuarioId")) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Falta usuarioId", 
                       "errors", Map.of("usuarioId", "Requerido")));
        }

        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            ParticipanteConversacion participante = 
                conversacionService.agregarParticipante(conversacionId, usuarioId);
            
            if (participante == null) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Error al agregar participante", 
                           "errors", Map.of("general", "Conversación no es grupal o usuario no existe")));
            }

            return ResponseEntity.status(201).body(participante);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al agregar participante", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Salir de conversación
    @DeleteMapping("/{conversacionId}/participantes/{usuarioId}")
    public ResponseEntity<?> salirDeConversacion(@PathVariable Long conversacionId,
                                                  @PathVariable Long usuarioId) {
        boolean exito = conversacionService.salirDeConversacion(conversacionId, usuarioId);
        
        if (!exito) {
            return ResponseEntity.status(404).body(
                Map.of("message", "Participante no encontrado", 
                       "errors", Map.of("usuarioId", "No es participante de esta conversación")));
        }

        return ResponseEntity.status(204).build();
    }

    // Marcar conversación como leída
    @PutMapping("/{conversacionId}/marcar-leido")
    public ResponseEntity<?> marcarComoLeido(@PathVariable Long conversacionId,
                                              @RequestBody Map<String, Object> body) {
        if (!body.containsKey("usuarioId")) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Falta usuarioId", 
                       "errors", Map.of("usuarioId", "Requerido")));
        }

        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            conversacionService.marcarComoLeido(conversacionId, usuarioId);
            return ResponseEntity.ok(Map.of("message", "Marcado como leído"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al marcar como leído", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }
}
