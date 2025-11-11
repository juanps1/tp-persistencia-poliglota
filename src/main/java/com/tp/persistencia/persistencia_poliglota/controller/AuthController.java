package com.tp.persistencia.persistencia_poliglota.controller;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.model.sql.Rol;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import com.tp.persistencia.persistencia_poliglota.repository.RolRepository;
import com.tp.persistencia.persistencia_poliglota.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolRepository rolRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Email es requerido", "errors", Map.of("email", "Campo obligatorio")));
        }
        if (loginRequest.getContrasena() == null || loginRequest.getContrasena().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Contraseña es requerida", "errors", Map.of("contrasena", "Campo obligatorio")));
        }
        Usuario user = usuarioRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).body(
                Map.of("message", "Credenciales inválidas", "errors", Map.of("email", "Email o contraseña incorrectos")));
        }

        boolean matchesHashed = passwordEncoder.matches(loginRequest.getContrasena(), user.getContrasena());
        boolean matchesPlain = user.getContrasena().equals(loginRequest.getContrasena());

        if (!matchesHashed && !matchesPlain) {
            return ResponseEntity.status(401).body(
                Map.of("message", "Credenciales inválidas", "errors", Map.of("email", "Email o contraseña incorrectos")));
        }

        // Si coincidió en texto plano y no es hash, actualizar a hash.
        if (matchesPlain && !matchesHashed) {
            user.setContrasena(passwordEncoder.encode(user.getContrasena()));
            usuarioRepository.save(user);
        }

        String token = JwtUtil.generateToken(user.getEmail());
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("token", token);
        respuesta.put("usuarioId", user.getId());
        respuesta.put("email", user.getEmail());
        respuesta.put("nombreCompleto", user.getNombreCompleto());
        if (user.getRol() != null) {
            respuesta.put("rolId", user.getRol().getId());
            respuesta.put("rol", user.getRol().getDescripcion());
        }
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario body) {
        // Validaciones mínimas
        if (body.getEmail() == null || body.getEmail().isBlank()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Email es requerido", "errors", Map.of("email", "Campo obligatorio")));
        }
        if (body.getContrasena() == null || body.getContrasena().isBlank()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Contraseña es requerida", "errors", Map.of("contrasena", "Campo obligatorio")));
        }
        if (body.getNombreCompleto() == null || body.getNombreCompleto().isBlank()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Nombre completo es requerido", "errors", Map.of("nombreCompleto", "Campo obligatorio")));
        }

        // Email duplicado
        if (usuarioRepository.findByEmail(body.getEmail()).isPresent()) {
            return ResponseEntity.status(409).body(
                Map.of("message", "Email duplicado", "errors", Map.of("email", "Ya existe un usuario con ese email")));
        }

        // Forzar rol USER (roleId = 2)
        Rol rolUser = rolRepository.findById(2L).orElse(null);
        if (rolUser == null) {
            rolUser = rolRepository.findByDescripcion("USER");
            if (rolUser == null) {
                rolUser = rolRepository.save(new Rol(null, "USER"));
            }
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombreCompleto(body.getNombreCompleto());
        nuevo.setEmail(body.getEmail());
        nuevo.setContrasena(passwordEncoder.encode(body.getContrasena()));
        nuevo.setEstado("activo");
        nuevo.setRol(rolUser);

        Usuario guardado = usuarioRepository.save(nuevo);

        String token = JwtUtil.generateToken(guardado.getEmail());
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("token", token);
        respuesta.put("usuarioId", guardado.getId());
        respuesta.put("email", guardado.getEmail());
        respuesta.put("nombreCompleto", guardado.getNombreCompleto());
        if (guardado.getRol() != null) {
            respuesta.put("rolId", guardado.getRol().getId());
            respuesta.put("rol", guardado.getRol().getDescripcion());
        }
        return ResponseEntity.status(201).body(respuesta);
    }
}
