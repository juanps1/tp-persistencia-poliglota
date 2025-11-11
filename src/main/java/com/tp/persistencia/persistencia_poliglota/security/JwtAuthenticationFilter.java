package com.tp.persistencia.persistencia_poliglota.security;

import com.tp.persistencia.persistencia_poliglota.model.sql.Usuario;
import com.tp.persistencia.persistencia_poliglota.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String email = JwtUtil.getUsernameFromToken(token);
                Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
                
                if (usuario != null) {
                    String rolDescripcion = usuario.getRol() != null ? usuario.getRol().getDescripcion() : "USER";
                    // Normalizar a mayúsculas para que coincida con SecurityConfig
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolDescripcion.toUpperCase());
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Token inválido, continuar sin autenticación
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
