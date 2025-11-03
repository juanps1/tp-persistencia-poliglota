package com.tp.persistencia.persistencia_poliglota.controller;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import com.tp.persistencia.persistencia_poliglota.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public String login(@RequestBody Usuario loginRequest) {
        Usuario user = usuarioRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (user != null && user.getContrasena().equals(loginRequest.getContrasena())) {
            return JwtUtil.generateToken(user.getEmail());
        } else {
            return "Credenciales inválidas";
        }
    }
}
