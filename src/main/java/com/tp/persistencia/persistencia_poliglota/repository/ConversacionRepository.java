package com.tp.persistencia.persistencia_poliglota.repository;

import com.tp.persistencia.persistencia_poliglota.model.sql.Conversacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ConversacionRepository extends JpaRepository<Conversacion, Long> {
    
    // Encontrar conversaciones donde un usuario es participante
    @Query("SELECT c FROM Conversacion c " +
           "JOIN ParticipanteConversacion pc ON pc.conversacion.id = c.id " +
           "WHERE pc.usuario.id = :usuarioId AND pc.activo = true " +
           "ORDER BY c.ultimaActividad DESC")
    List<Conversacion> findConversacionesByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // Encontrar conversación privada entre dos usuarios
    @Query("SELECT c FROM Conversacion c " +
           "WHERE c.tipo = 'privada' AND c.id IN (" +
           "  SELECT pc1.conversacion.id FROM ParticipanteConversacion pc1 " +
           "  JOIN ParticipanteConversacion pc2 ON pc1.conversacion.id = pc2.conversacion.id " +
           "  WHERE pc1.usuario.id = :usuario1Id AND pc2.usuario.id = :usuario2Id " +
           "  AND pc1.activo = true AND pc2.activo = true" +
           ")")
    Conversacion findConversacionPrivadaEntreUsuarios(@Param("usuario1Id") Long usuario1Id, 
                                                       @Param("usuario2Id") Long usuario2Id);
}
