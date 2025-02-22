package com.backend.flags.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;

import com.backend.flags.models.Flag;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@RestController
public class FlagsController {

    @GetMapping("/api/flags")
    public List<Flag> getFlags() {
        try {
            // Carrega o arquivo JSON de bandeiras
            ClassPathResource resource = new ClassPathResource("assets/flags.json");
            ObjectMapper objectMapper = new ObjectMapper();
            
            // Converte o JSON para uma lista de objetos Flag
            List<Flag> flagsList = objectMapper.readValue(
                    resource.getInputStream(), 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Flag.class)
            );
        
            return flagsList;
        
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar as bandeiras: " + e.getMessage());
        }
    }
}