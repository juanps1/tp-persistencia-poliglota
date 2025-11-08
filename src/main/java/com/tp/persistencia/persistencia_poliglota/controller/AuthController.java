package com.tp.persistencia.persistencia_poliglota.controller;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import com.tp.persistencia.persistencia_poliglota.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
        if (user != null && user.getContrasena().equals(loginRequest.getContrasena())) {
            return ResponseEntity.ok(Map.of("token", JwtUtil.generateToken(user.getEmail())));
        } else {
            return ResponseEntity.status(401).body(
                Map.of("message", "Credenciales inválidas", "errors", Map.of("email", "Email o contraseña incorrectos")));
        }
    }
}
