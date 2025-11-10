package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Mensaje;
import com.tp.persistencia.persistencia_poliglota.service.ConversacionService;
import com.tp.persistencia.persistencia_poliglota.service.MensajeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    private final MensajeService mensajeService;
    private final ConversacionService conversacionService;

    public MensajeController(MensajeService mensajeService, 
                             ConversacionService conversacionService) {
        this.mensajeService = mensajeService;
        this.conversacionService = conversacionService;
    }

    // Enviar mensaje
    @PostMapping
    public ResponseEntity<?> enviarMensaje(@RequestBody Map<String, Object> body) {
        // Validaciones
        if (!body.containsKey("conversacionId") || !body.containsKey("remitenteId") 
            || !body.containsKey("contenido")) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Faltan campos requeridos", 
                       "errors", Map.of("conversacionId", "Requerido", 
                                       "remitenteId", "Requerido",
                                       "contenido", "Requerido")));
        }

        try {
            Long conversacionId = Long.valueOf(body.get("conversacionId").toString());
            Long remitenteId = Long.valueOf(body.get("remitenteId").toString());
            String contenido = body.get("contenido").toString();

            if (contenido.trim().isEmpty()) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Contenido inválido", 
                           "errors", Map.of("contenido", "No puede estar vacío")));
            }

            // Verificar que el remitente es participante
            if (!conversacionService.esParticipante(conversacionId, remitenteId)) {
                return ResponseEntity.status(403).body(
                    Map.of("message", "No autorizado", 
                           "errors", Map.of("remitenteId", "No eres participante de esta conversación")));
            }

            Mensaje mensaje = mensajeService.enviarMensaje(conversacionId, remitenteId, contenido);
            
            if (mensaje == null) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Error al enviar mensaje", 
                           "errors", Map.of("general", "Conversación o remitente no encontrado")));
            }

            return ResponseEntity.status(201).body(mensaje);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al enviar mensaje", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Obtener mensajes de una conversación
    @GetMapping("/conversacion/{conversacionId}")
    public ResponseEntity<?> obtenerMensajes(@PathVariable Long conversacionId,
                                              @RequestParam(required = false) Long usuarioId) {
        try {
            // Verificar que el usuario es participante (si se proporciona)
            if (usuarioId != null && !conversacionService.esParticipante(conversacionId, usuarioId)) {
                return ResponseEntity.status(403).body(
                    Map.of("message", "No autorizado", 
                           "errors", Map.of("usuarioId", "No eres participante de esta conversación")));
            }

            List<Mensaje> mensajes = mensajeService.obtenerMensajesDeConversacion(conversacionId);
            return ResponseEntity.ok(mensajes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al obtener mensajes", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Editar mensaje
    @PutMapping("/{mensajeId}")
    public ResponseEntity<?> editarMensaje(@PathVariable Long mensajeId,
                                            @RequestBody Map<String, Object> body) {
        if (!body.containsKey("usuarioId") || !body.containsKey("contenido")) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Faltan campos requeridos", 
                       "errors", Map.of("usuarioId", "Requerido", "contenido", "Requerido")));
        }

        try {
            Long usuarioId = Long.valueOf(body.get("usuarioId").toString());
            String contenido = body.get("contenido").toString();

            if (contenido.trim().isEmpty()) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Contenido inválido", 
                           "errors", Map.of("contenido", "No puede estar vacío")));
            }

            Mensaje mensaje = mensajeService.editarMensaje(mensajeId, usuarioId, contenido);
            
            if (mensaje == null) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Error al editar mensaje", 
                           "errors", Map.of("general", "Mensaje no encontrado, no autorizado o ya eliminado")));
            }

            return ResponseEntity.ok(mensaje);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al editar mensaje", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Eliminar mensaje
    @DeleteMapping("/{mensajeId}")
    public ResponseEntity<?> eliminarMensaje(@PathVariable Long mensajeId,
                                              @RequestParam Long usuarioId) {
        try {
            boolean exito = mensajeService.eliminarMensaje(mensajeId, usuarioId);
            
            if (!exito) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Error al eliminar mensaje", 
                           "errors", Map.of("general", "Mensaje no encontrado o no autorizado")));
            }

            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al eliminar mensaje", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }

    // Obtener un mensaje específico
    @GetMapping("/{mensajeId}")
    public ResponseEntity<?> obtenerMensaje(@PathVariable Long mensajeId) {
        Optional<Mensaje> mensaje = mensajeService.obtenerMensajePorId(mensajeId);
        
        if (mensaje.isEmpty()) {
            return ResponseEntity.status(404).body(
                Map.of("message", "Mensaje no encontrado", 
                       "errors", Map.of("mensajeId", "No existe")));
        }

        return ResponseEntity.ok(mensaje.get());
    }

    // Contar mensajes no leídos
    @GetMapping("/no-leidos")
    public ResponseEntity<?> contarMensajesNoLeidos(@RequestParam Long conversacionId,
                                                      @RequestParam Long usuarioId) {
        try {
            Long cantidad = mensajeService.contarMensajesNoLeidos(conversacionId, usuarioId);
            return ResponseEntity.ok(Map.of("cantidad", cantidad));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("message", "Error al contar mensajes no leídos", 
                       "errors", Map.of("general", e.getMessage())));
        }
    }
}
