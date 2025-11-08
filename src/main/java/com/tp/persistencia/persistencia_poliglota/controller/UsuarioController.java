package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.service.UsuarioService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listarUsuarios();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Usuario usuario) {
        // Validación básica de campos
        if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Email es requerido", "errors", Map.of("email", "Campo obligatorio")));
        }
        if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Contraseña es requerida", "errors", Map.of("contrasena", "Campo obligatorio")));
        }
        if (usuario.getNombreCompleto() == null || usuario.getNombreCompleto().isEmpty()) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Nombre completo es requerido", "errors", Map.of("nombreCompleto", "Campo obligatorio")));
        }
        if (usuario.getEstado() == null || !(usuario.getEstado().equals("activo") || usuario.getEstado().equals("inactivo"))) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Estado inválido", "errors", Map.of("estado", "Debe ser 'activo' o 'inactivo'")));
        }
        if (usuario.getRol() == null || usuario.getRol().getId() == null) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Rol es requerido", "errors", Map.of("rol", "Debe enviar objeto {id}")));
        }

        // Email duplicado
        if (usuarioService.existeEmail(usuario.getEmail())) {
            return ResponseEntity.status(409).body(
                Map.of("message", "Email duplicado", "errors", Map.of("email", "Ya existe un usuario con ese email")));
        }

        Usuario guardado = usuarioService.guardarUsuario(usuario);
        // Excluir contraseña en respuesta
        guardado.setContrasena(null);
        return ResponseEntity.status(201).body(guardado);
    }
}
