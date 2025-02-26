package com.backend.flags.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.flags.models.WikiEconomicsInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WikipediaEconomicService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext=true&titles=";

    public WikipediaEconomicService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public WikiEconomicsInfo getCountryEconomicInfo(String country) {
        String formattedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8).replace("+", "%20");
        URI uri = URI.create(WIKIPEDIA_API_URL + formattedCountry + "&format=json");

        System.out.println("[0] URL de api/wikipedia/{country}/economics montada: " + uri);

        try {
            String jsonResponse = restTemplate.getForObject(uri, String.class);
            System.out.println("[1] Resposta da api/wikipedia/{country}/economics: " + jsonResponse);

            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode pages = root.path("query").path("pages");

            if (!pages.fieldNames().hasNext()) {
                System.out.println("[2] Nenhuma página encontrada.");
                return new WikiEconomicsInfo(country, "section", "Conteúdo não disponível.");
            }

            String pageId = pages.fieldNames().next();
            JsonNode page = pages.path(pageId);
            String title = page.path("title").asText("Título não disponível.");
            String extract = page.path("extract").asText(null);

            if (extract == null || extract.isEmpty()) {
                return new WikiEconomicsInfo(title, "section", "Conteúdo econômico não disponível.");
            }

            // Aqui tentamos encontrar a parte de economia ou política no texto
            String economicContent = extractEconomicSection(extract);

            return new WikiEconomicsInfo(title, "section", economicContent);
        } catch (Exception e) {
            System.err.println("[3] Erro ao obter dados econômicos da Wikipedia: " + e.getMessage());
            return new WikiEconomicsInfo("Erro", "Não foi possível obter informações.", "Erro ao buscar dados.");
        }
    }

    private String extractEconomicSection(String text) {
        String[] sections = text.split("\n");
        StringBuilder economicContent = new StringBuilder();
        boolean isEconomicSection = false;

        for (String line : sections) {
            if (line.toLowerCase().contains("economy") || line.toLowerCase().contains("politics")) {
                isEconomicSection = true;
            } else if (isEconomicSection && line.matches("^[A-Z][a-z]+.*")) {
                break; // Sai ao encontrar outro título
            }

            if (isEconomicSection) {
                economicContent.append(line).append("\n");
            }
        }
        return economicContent.toString().trim().isEmpty() ? "Informação econômica não encontrada." : economicContent.toString().trim();
    }
}
