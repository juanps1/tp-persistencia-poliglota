package com.tp.persistencia.persistencia_poliglota.repository;

import com.tp.persistencia.persistencia_poliglota.model.sql.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    // Obtener mensajes de una conversación (solo no eliminados)
    List<Mensaje> findByConversacionIdAndEliminadoFalseOrderByFechaEnvioAsc(Long conversacionId);
    
    // Obtener últimos N mensajes de una conversación
    @Query("SELECT m FROM Mensaje m " +
           "WHERE m.conversacion.id = :conversacionId AND m.eliminado = false " +
           "ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findUltimosMensajes(@Param("conversacionId") Long conversacionId);
    
    // Contar mensajes no leídos para un usuario en una conversación
    @Query("SELECT COUNT(m) FROM Mensaje m " +
           "JOIN ParticipanteConversacion pc ON pc.conversacion.id = m.conversacion.id " +
           "WHERE m.conversacion.id = :conversacionId " +
           "AND pc.usuario.id = :usuarioId " +
           "AND m.remitente.id != :usuarioId " +
           "AND m.eliminado = false " +
           "AND (pc.ultimaLectura IS NULL OR m.fechaEnvio > pc.ultimaLectura)")
    Long countMensajesNoLeidos(@Param("conversacionId") Long conversacionId, 
                                @Param("usuarioId") Long usuarioId);
}
