package com.tp.persistencia.persistencia_poliglota.controller;

import com.tp.persistencia.persistencia_poliglota.model.sql.Rol;
import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.RolRepository;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import com.tp.persistencia.persistencia_poliglota.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, RolRepository rolRepository, PasswordEncoder passwordEncoder, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Usuario> listar() {
        // Sanear contraseñas
        return usuarioService.listarUsuarios().stream().map(u -> {
            u.setContrasena(null);
            return u;
        }).collect(Collectors.toList());
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
        if (usuario.getRol() == null ||
            (usuario.getRol().getId() == null && (usuario.getRol().getDescripcion() == null || usuario.getRol().getDescripcion().isBlank()))) {
            return ResponseEntity.status(400).body(
                Map.of("message", "Rol es requerido", "errors", Map.of("rol", "Debe enviar objeto {id} o {descripcion}")));
        }

        // Email duplicado
        if (usuarioService.existeEmail(usuario.getEmail())) {
            return ResponseEntity.status(409).body(
                Map.of("message", "Email duplicado", "errors", Map.of("email", "Ya existe un usuario con ese email")));
        }

        // Resolver rol
        Rol rolSeleccionado = null;
        if (usuario.getRol().getId() != null) {
            Long rolId = usuario.getRol().getId();
            rolSeleccionado = rolRepository.findById(Objects.requireNonNull(rolId)).orElse(null);
            if (rolSeleccionado == null) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Rol inexistente", "errors", Map.of("rol.id", "No existe rol con id=" + usuario.getRol().getId())));
            }
        } else if (usuario.getRol().getDescripcion() != null) {
            rolSeleccionado = rolRepository.findByDescripcion(usuario.getRol().getDescripcion());
            if (rolSeleccionado == null) {
                return ResponseEntity.status(400).body(
                    Map.of("message", "Rol inexistente", "errors", Map.of("rol.descripcion", "No existe rol con descripcion='" + usuario.getRol().getDescripcion() + "'")));
            }
        }
        usuario.setRol(rolSeleccionado);

        // Hash de contraseña
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        Usuario guardado = usuarioService.guardarUsuario(usuario);
        // Excluir contraseña en respuesta
        guardado.setContrasena(null);
        return ResponseEntity.status(201).body(guardado);
    }

    @PatchMapping("/{id}/rol")
    public ResponseEntity<?> cambiarRol(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Object rolObj = body.get("rolId");
        if (rolObj == null) {
            return ResponseEntity.status(400).body(Map.of(
                "message", "rolId es requerido",
                "errors", Map.of("rolId", "Debe enviar un número (1=ADMIN,2=USER)")));
        }
        Long nuevoRolId;
        try {
            nuevoRolId = Long.valueOf(String.valueOf(rolObj));
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body(Map.of(
                "message", "rolId inválido",
                "errors", Map.of("rolId", "Debe ser numérico")));
        }
        if (!(nuevoRolId == 1L || nuevoRolId == 2L)) {
            return ResponseEntity.status(400).body(Map.of(
                "message", "rolId inválido",
                "errors", Map.of("rolId", "Sólo se admite 1 (ADMIN) o 2 (USER)")));
        }
        Usuario usuario = usuarioService.listarUsuarios().stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of(
                "message", "Usuario no encontrado",
                "errors", Map.of("id", "No existe usuario con id=" + id)));
        }
        Rol rol = rolRepository.findById(nuevoRolId).orElse(null);
        if (rol == null) {
            return ResponseEntity.status(500).body(Map.of(
                "message", "Roles base faltantes",
                "errors", Map.of("rolId", "Debe existir rol con id=" + nuevoRolId)));
        }
        usuario.setRol(rol);
        Usuario actualizado = usuarioService.guardarUsuario(usuario);
        actualizado.setContrasena(null);
        return ResponseEntity.ok(Map.of(
            "message", "Rol actualizado",
            "usuarioId", actualizado.getId(),
            "rolId", actualizado.getRol().getId(),
            "rol", actualizado.getRol().getDescripcion()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "No autenticado"));
        }
        String email = String.valueOf(auth.getPrincipal());
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Usuario no encontrado"));
        }
        Map<String, Object> data = Map.of(
            "id", usuario.getId(),
            "email", usuario.getEmail(),
            "nombreCompleto", usuario.getNombreCompleto(),
            "estado", usuario.getEstado(),
            "rolId", usuario.getRol() != null ? usuario.getRol().getId() : null,
            "rol", usuario.getRol() != null ? usuario.getRol().getDescripcion() : null
        );
        return ResponseEntity.ok(data);
    }
}
