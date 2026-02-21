package com.backend.flags.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    public AppConfig() {
        System.out.println(">>> [DIAGNÓSTICO] Classe AppConfig carregada pelo Spring!");
    }

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add((request, body, execution) -> {
            // Define o User-Agent globalmente para todas as requisições deste restTemplate
            request.getHeaders().set(HttpHeaders.USER_AGENT,
                    "https://github.com/nic-cavalheiro/flags (nicolasbcavalheiro@gmail.com)");
            return execution.execute(request, body);
        });

        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }
}
