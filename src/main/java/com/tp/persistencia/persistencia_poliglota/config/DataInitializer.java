package com.tp.persistencia.persistencia_poliglota.config;

import com.tp.persistencia.persistencia_poliglota.model.sql.Rol;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.RolRepository;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository, UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Normalizar roles: ID=1 -> ADMIN, ID=2 -> USER.
        // Si la tabla está vacía o faltan estos roles, recrearlos.
        Rol adminRole = rolRepository.findByDescripcion("ADMIN");
        if (adminRole == null) {
            adminRole = rolRepository.save(new Rol(null, "ADMIN"));
        }
        Rol userRole = rolRepository.findByDescripcion("USER");
        if (userRole == null) {
            userRole = rolRepository.save(new Rol(null, "USER"));
        }
        final Rol admin = adminRole; // efectivamente final

        // Crear usuario admin si no existe
        final String adminEmail = "admin@admin.com"; // usuario administrador por defecto
        usuarioRepository.findByEmail(adminEmail).orElseGet(() -> {
            Usuario u = new Usuario();
            u.setNombreCompleto("Administrador Principal");
            u.setEmail(adminEmail);
            u.setContrasena(passwordEncoder.encode("admin"));
            u.setEstado("activo");
            u.setRol(admin);
            return usuarioRepository.save(u);
        });
    }
}
