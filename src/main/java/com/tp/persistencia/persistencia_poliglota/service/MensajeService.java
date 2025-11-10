package com.tp.persistencia.persistencia_poliglota.service;

import com.tp.persistencia.persistencia_poliglota.model.sql.Conversacion;
import com.tp.persistencia.persistencia_poliglota.model.sql.Mensaje;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.ConversacionRepository;
import com.tp.persistencia.persistencia_poliglota.repository.MensajeRepository;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final ConversacionRepository conversacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConversacionService conversacionService;

    public MensajeService(MensajeRepository mensajeRepository,
                          ConversacionRepository conversacionRepository,
                          UsuarioRepository usuarioRepository,
                          ConversacionService conversacionService) {
        this.mensajeRepository = mensajeRepository;
        this.conversacionRepository = conversacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.conversacionService = conversacionService;
    }

    // Enviar mensaje
    @Transactional
    public Mensaje enviarMensaje(Long conversacionId, Long remitenteId, String contenido) {
        Conversacion conversacion = conversacionRepository.findById(conversacionId).orElse(null);
        Usuario remitente = usuarioRepository.findById(remitenteId).orElse(null);

        if (conversacion == null || remitente == null) {
            return null;
        }

        // Verificar que el remitente sea participante
        if (!conversacionService.esParticipante(conversacionId, remitenteId)) {
            return null;
        }

        Mensaje mensaje = new Mensaje();
        mensaje.setConversacion(conversacion);
        mensaje.setRemitente(remitente);
        mensaje.setContenido(contenido);
        mensaje.setFechaEnvio(LocalDateTime.now());
        mensaje.setEditado(false);
        mensaje.setEliminado(false);

        mensaje = mensajeRepository.save(mensaje);

        // Actualizar última actividad de la conversación
        conversacionService.actualizarUltimaActividad(conversacionId);

        return mensaje;
    }

    // Obtener mensajes de una conversación
    public List<Mensaje> obtenerMensajesDeConversacion(Long conversacionId) {
        return mensajeRepository.findByConversacionIdAndEliminadoFalseOrderByFechaEnvioAsc(conversacionId);
    }

    // Editar mensaje
    @Transactional
    public Mensaje editarMensaje(Long mensajeId, Long usuarioId, String nuevoContenido) {
        Optional<Mensaje> mensajeOpt = mensajeRepository.findById(mensajeId);
        
        if (mensajeOpt.isEmpty()) {
            return null;
        }

        Mensaje mensaje = mensajeOpt.get();

        // Verificar que el usuario sea el remitente
        if (!mensaje.getRemitente().getId().equals(usuarioId)) {
            return null;
        }

        // Verificar que no esté eliminado
        if (mensaje.getEliminado()) {
            return null;
        }

        mensaje.setContenido(nuevoContenido);
        mensaje.setEditado(true);
        mensaje.setFechaEdicion(LocalDateTime.now());

        return mensajeRepository.save(mensaje);
    }

    // Eliminar mensaje (borrado lógico)
    @Transactional
    public boolean eliminarMensaje(Long mensajeId, Long usuarioId) {
        Optional<Mensaje> mensajeOpt = mensajeRepository.findById(mensajeId);
        
        if (mensajeOpt.isEmpty()) {
            return false;
        }

        Mensaje mensaje = mensajeOpt.get();

        // Verificar que el usuario sea el remitente
        if (!mensaje.getRemitente().getId().equals(usuarioId)) {
            return false;
        }

        mensaje.setEliminado(true);
        mensajeRepository.save(mensaje);

        return true;
    }

    // Obtener mensaje por ID
    public Optional<Mensaje> obtenerMensajePorId(Long mensajeId) {
        return mensajeRepository.findById(mensajeId);
    }

    // Contar mensajes no leídos
    public Long contarMensajesNoLeidos(Long conversacionId, Long usuarioId) {
        return mensajeRepository.countMensajesNoLeidos(conversacionId, usuarioId);
    }
}
