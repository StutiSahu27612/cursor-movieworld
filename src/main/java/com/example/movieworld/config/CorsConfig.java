package com.example.movieworld.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow all origins (for development)
        // In production, specify your frontend URL
        // Spring Boot 3.x: use addAllowedOriginPattern instead of addAllowedOrigin for
        // wildcards
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        // Note: setAllowCredentials(true) cannot be used with wildcard origin patterns
        // If you need credentials, specify exact origins instead

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
