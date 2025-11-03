package com.tp.persistencia.persistencia_poliglota.repository;
import com.tp.persistencia.persistencia_poliglota.model.sql.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Rol findByDescripcion(String descripcion);
}
