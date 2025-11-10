package com.tp.persistencia.persistencia_poliglota.service;

import com.tp.persistencia.persistencia_poliglota.model.sql.Conversacion;
import com.tp.persistencia.persistencia_poliglota.model.sql.ParticipanteConversacion;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.ConversacionRepository;
import com.tp.persistencia.persistencia_poliglota.repository.ParticipanteConversacionRepository;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConversacionService {

    private final ConversacionRepository conversacionRepository;
    private final ParticipanteConversacionRepository participanteRepository;
    private final UsuarioRepository usuarioRepository;

    public ConversacionService(ConversacionRepository conversacionRepository,
                               ParticipanteConversacionRepository participanteRepository,
                               UsuarioRepository usuarioRepository) {
        this.conversacionRepository = conversacionRepository;
        this.participanteRepository = participanteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Obtener todas las conversaciones de un usuario
    public List<Conversacion> obtenerConversacionesUsuario(Long usuarioId) {
        return conversacionRepository.findConversacionesByUsuarioId(usuarioId);
    }

    // Crear conversación privada entre dos usuarios
    @Transactional
    public Conversacion crearConversacionPrivada(Long usuario1Id, Long usuario2Id) {
        // Verificar si ya existe conversación privada entre estos usuarios
        Conversacion existente = conversacionRepository
            .findConversacionPrivadaEntreUsuarios(usuario1Id, usuario2Id);
        
        if (existente != null) {
            return existente;
        }

        // Obtener usuarios
        Usuario usuario1 = usuarioRepository.findById(usuario1Id).orElse(null);
        Usuario usuario2 = usuarioRepository.findById(usuario2Id).orElse(null);
        
        if (usuario1 == null || usuario2 == null) {
            return null;
        }

        // Crear conversación
        Conversacion conversacion = new Conversacion();
        conversacion.setTipo("privada");
        conversacion.setCreador(usuario1);
        conversacion.setFechaCreacion(LocalDateTime.now());
        conversacion.setUltimaActividad(LocalDateTime.now());
        conversacion = conversacionRepository.save(conversacion);

        // Agregar participantes
        ParticipanteConversacion p1 = new ParticipanteConversacion();
        p1.setConversacion(conversacion);
        p1.setUsuario(usuario1);
        p1.setFechaIngreso(LocalDateTime.now());
        p1.setActivo(true);
        participanteRepository.save(p1);

        ParticipanteConversacion p2 = new ParticipanteConversacion();
        p2.setConversacion(conversacion);
        p2.setUsuario(usuario2);
        p2.setFechaIngreso(LocalDateTime.now());
        p2.setActivo(true);
        participanteRepository.save(p2);

        return conversacion;
    }

    // Crear conversación grupal
    @Transactional
    public Conversacion crearConversacionGrupal(String nombre, Long creadorId, List<Long> participantesIds) {
        Usuario creador = usuarioRepository.findById(creadorId).orElse(null);
        if (creador == null) {
            return null;
        }

        // Crear conversación
        Conversacion conversacion = new Conversacion();
        conversacion.setTipo("grupal");
        conversacion.setNombre(nombre);
        conversacion.setCreador(creador);
        conversacion.setFechaCreacion(LocalDateTime.now());
        conversacion.setUltimaActividad(LocalDateTime.now());
        conversacion = conversacionRepository.save(conversacion);

        // Agregar creador como participante
        ParticipanteConversacion pCreador = new ParticipanteConversacion();
        pCreador.setConversacion(conversacion);
        pCreador.setUsuario(creador);
        pCreador.setFechaIngreso(LocalDateTime.now());
        pCreador.setActivo(true);
        participanteRepository.save(pCreador);

        // Agregar demás participantes
        if (participantesIds != null) {
            for (Long participanteId : participantesIds) {
                if (!participanteId.equals(creadorId)) {
                    Usuario usuario = usuarioRepository.findById(participanteId).orElse(null);
                    if (usuario != null) {
                        ParticipanteConversacion p = new ParticipanteConversacion();
                        p.setConversacion(conversacion);
                        p.setUsuario(usuario);
                        p.setFechaIngreso(LocalDateTime.now());
                        p.setActivo(true);
                        participanteRepository.save(p);
                    }
                }
            }
        }

        return conversacion;
    }

    // Obtener conversación por ID
    public Optional<Conversacion> obtenerConversacionPorId(Long id) {
        return conversacionRepository.findById(id);
    }

    // Verificar si usuario es participante de la conversación
    public boolean esParticipante(Long conversacionId, Long usuarioId) {
        Optional<ParticipanteConversacion> participante = 
            participanteRepository.findByConversacionIdAndUsuarioId(conversacionId, usuarioId);
        return participante.isPresent() && participante.get().getActivo();
    }

    // Obtener participantes de una conversación
    public List<ParticipanteConversacion> obtenerParticipantes(Long conversacionId) {
        return participanteRepository.findByConversacionIdAndActivoTrue(conversacionId);
    }

    // Agregar participante a conversación grupal
    @Transactional
    public ParticipanteConversacion agregarParticipante(Long conversacionId, Long usuarioId) {
        Conversacion conversacion = conversacionRepository.findById(conversacionId).orElse(null);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        
        if (conversacion == null || usuario == null || !conversacion.getTipo().equals("grupal")) {
            return null;
        }

        // Verificar si ya es participante
        Optional<ParticipanteConversacion> existente = 
            participanteRepository.findByConversacionIdAndUsuarioId(conversacionId, usuarioId);
        
        if (existente.isPresent()) {
            ParticipanteConversacion p = existente.get();
            p.setActivo(true);
            return participanteRepository.save(p);
        }

        // Crear nuevo participante
        ParticipanteConversacion participante = new ParticipanteConversacion();
        participante.setConversacion(conversacion);
        participante.setUsuario(usuario);
        participante.setFechaIngreso(LocalDateTime.now());
        participante.setActivo(true);
        
        return participanteRepository.save(participante);
    }

    // Salir de conversación (desactivar participante)
    @Transactional
    public boolean salirDeConversacion(Long conversacionId, Long usuarioId) {
        Optional<ParticipanteConversacion> participante = 
            participanteRepository.findByConversacionIdAndUsuarioId(conversacionId, usuarioId);
        
        if (participante.isEmpty()) {
            return false;
        }

        ParticipanteConversacion p = participante.get();
        p.setActivo(false);
        participanteRepository.save(p);
        
        return true;
    }

    // Actualizar última actividad de la conversación
    @Transactional
    public void actualizarUltimaActividad(Long conversacionId) {
        conversacionRepository.findById(conversacionId).ifPresent(conversacion -> {
            conversacion.setUltimaActividad(LocalDateTime.now());
            conversacionRepository.save(conversacion);
        });
    }

    // Marcar como leído (actualizar ultimaLectura del participante)
    @Transactional
    public void marcarComoLeido(Long conversacionId, Long usuarioId) {
        Optional<ParticipanteConversacion> participante = 
            participanteRepository.findByConversacionIdAndUsuarioId(conversacionId, usuarioId);
        
        if (participante.isPresent()) {
            ParticipanteConversacion p = participante.get();
            p.setUltimaLectura(LocalDateTime.now());
            participanteRepository.save(p);
        }
    }
}
