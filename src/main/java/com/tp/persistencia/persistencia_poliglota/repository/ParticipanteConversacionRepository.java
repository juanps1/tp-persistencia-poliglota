package com.tp.persistencia.persistencia_poliglota.repository;

import com.tp.persistencia.persistencia_poliglota.model.sql.ParticipanteConversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ParticipanteConversacionRepository extends JpaRepository<ParticipanteConversacion, Long> {
    
    // Obtener todos los participantes de una conversación
    List<ParticipanteConversacion> findByConversacionIdAndActivoTrue(Long conversacionId);
    
    // Verificar si un usuario es participante de una conversación
    @Query("SELECT pc FROM ParticipanteConversacion pc " +
           "WHERE pc.conversacion.id = :conversacionId AND pc.usuario.id = :usuarioId")
    Optional<ParticipanteConversacion> findByConversacionIdAndUsuarioId(
        @Param("conversacionId") Long conversacionId, 
        @Param("usuarioId") Long usuarioId);
    
    // Contar participantes activos de una conversación
    Long countByConversacionIdAndActivoTrue(Long conversacionId);
    
    // Obtener conversaciones de un usuario
    List<ParticipanteConversacion> findByUsuarioIdAndActivoTrue(Long usuarioId);
}
