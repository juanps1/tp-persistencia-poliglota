package com.tp.persistencia.persistencia_poliglota.config;

import com.tp.persistencia.persistencia_poliglota.model.sql.Rol;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.RolRepository;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        // Crear rol 'admin' si no existe
        Rol adminRole = rolRepository.findByDescripcion("admin");
        if (adminRole == null) {
            adminRole = rolRepository.save(new Rol(null, "admin"));
        }
        final Rol admin = adminRole; // usar variable efectivamente final en la lambda

        // Crear usuario admin si no existe
        final String adminEmail = "admin@admin.com";
        usuarioRepository.findByEmail(adminEmail).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNombreCompleto("admin");
            u.setEmail(adminEmail);
            // NOTA: contraseña en texto plano, pendiente agregar hashing (BCrypt) más adelante
            u.setContrasena("admin");
            u.setEstado("activo");
            u.setRol(admin);
            return usuarioRepository.save(u);
        });
    }
}
