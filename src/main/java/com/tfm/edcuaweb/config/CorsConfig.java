package com.tfm.edcuaweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Permitir solo tu frontend Angular durante desarrollo
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        // Métodos permitidos (muy importante incluir OPTIONS)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Permitir encabezados usados en JWT y peticiones
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));
        // Permitir cookies o credenciales (útil si usas HttpOnly o refresh tokens)
        config.setAllowCredentials(true);
        // Registrar la configuración para todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
