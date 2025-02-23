package com.backend.flags.config;

// import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@SuppressWarnings("null") CorsRegistry registry) {
        // Permite acesso de todas as origens (*)
        registry.addMapping("/api/**")  // Mapeia os endpoints de sua API
                .allowedOrigins("http://localhost:4200")  // Origem permitida (onde o Angular está rodando)
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Métodos permitidos
                .allowedHeaders("*");  // Cabeçalhos permitidos
    }
}
