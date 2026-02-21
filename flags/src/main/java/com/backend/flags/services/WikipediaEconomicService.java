package com.backend.flags.services;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backend.flags.models.WikiEconomicsInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class WikipediaEconomicService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final WikipediaService wikipediaService;

    private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext=true&titles=";

    public WikipediaEconomicService(RestTemplate restTemplate, ObjectMapper objectMapper,
            WikipediaService wikipediaService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.wikipediaService = wikipediaService;
    }

    public WikiEconomicsInfo getCountryEconomicInfo(String country) {
        String formattedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8).replace("+", "%20");
        URI uri = URI.create(WIKIPEDIA_API_URL + formattedCountry + "&format=json");

        try {
            String jsonResponse = restTemplate.getForObject(uri, String.class);

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode pages = root.path("query").path("pages");

            if (!pages.fieldNames().hasNext()) {
                return new WikiEconomicsInfo(country, "section", "Conteúdo não disponível.");
            }

            String pageId = pages.fieldNames().next();
            JsonNode page = pages.path(pageId);
            String title = page.path("title").asText("Título não disponível.");
            String extract = page.path("extract").asText(null);

            if (extract == null || extract.isEmpty()) {
                String redirectedTitle = wikipediaService.fetchRedirectedTitle(formattedCountry);

                if (redirectedTitle != null && !redirectedTitle.equalsIgnoreCase(country)) {
                    return this.getCountryEconomicInfo(redirectedTitle); // Usando `this` em vez de
                                                                         // `wikipediaEconomicService`
                } else {
                    return new WikiEconomicsInfo(title, "section", "Conteúdo econômico não disponível.");
                }
            }

            String economicContent = extractEconomicSection(extract);
            return new WikiEconomicsInfo(title, "section", economicContent);

        } catch (Exception e) {
            System.err.println("[3] Erro ao obter dados econômicos da Wikipedia: " + e.getMessage());
            return new WikiEconomicsInfo("Erro", "Não foi possível obter informações.", "Erro ao buscar dados.");
        }
    }

    private String extractEconomicSection(String text) {
        // Normaliza quebras de linha
        String normalizedText = text.replaceAll("\\r\\n?", "\n");
        String[] lines = normalizedText.split("\n");

        StringBuilder economicContent = new StringBuilder();
        boolean isInEconomicSection = false;

        for (String line : lines) {
            String trimmedLine = line.trim();
            String upperLine = trimmedLine.toUpperCase();

            // Início da seção econômica ou política
            if (upperLine.contains("ECONOMY") || upperLine.contains("ECONOMIC") ||
                    upperLine.contains("POLITICS") || upperLine.contains("POLITICAL")) {
                isInEconomicSection = true;
            }

            // Fim da seção: encontrou um novo título do tipo == Title ==
            else if (isInEconomicSection && trimmedLine.matches("^==+\\s?.+\\s?==+$")) {
                break;
            }

            // Adiciona linha se estamos dentro da seção desejada
            if (isInEconomicSection) {
                economicContent.append(line).append("\n");
            }
        }

        String result = economicContent.toString().trim();
        return result.isEmpty() ? "Informação econômica não encontrada." : result;
    }

}
