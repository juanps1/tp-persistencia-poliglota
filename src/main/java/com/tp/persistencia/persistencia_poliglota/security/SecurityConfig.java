package com.tp.persistencia.persistencia_poliglota.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // login libre
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                // Endpoint para obtener el usuario actual (requiere estar autenticado, cualquier rol)
                .requestMatchers("/api/usuarios/me").authenticated()
                // Administración de usuarios y roles sólo para ADMIN
                .requestMatchers("/api/usuarios/**").hasAuthority("ADMIN")
                // Alertas - reglas de acceso
                .requestMatchers(HttpMethod.DELETE, "/api/alertas/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/alertas").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/alertas/**").authenticated()
                // Solo administradores pueden eliminar sensores y mediciones
                .requestMatchers(HttpMethod.DELETE, "/api/sensores/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/mediciones/**").hasAuthority("ADMIN")
                .anyRequest().permitAll() // el resto es público por ahora
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

