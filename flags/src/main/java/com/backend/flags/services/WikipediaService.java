
package com.backend.flags.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.flags.models.WikipediaSummary;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;

@Service
public class WikipediaService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    public static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exintro=true&explaintext=true&titles=";

    public WikipediaService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public WikipediaSummary getCountrySummary(String country) {
        String formattedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8).replace("+", "%20");
        URI uri = URI.create(WIKIPEDIA_API_URL + formattedCountry + "&format=json");

        try {
            String jsonResponse = restTemplate.getForObject(uri, String.class);
            // System.out.println("[1] Resposta da API com URL: "+ uri + jsonResponse);

            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode pages = root.path("query").path("pages");

            if (!pages.fieldNames().hasNext()) {
                return new WikipediaSummary(country, "Conteúdo não disponível.");
            }

            String pageId = pages.fieldNames().next();
            JsonNode page = pages.path(pageId);
            String title = page.path("title").asText("Título não disponível.");
            String extract = page.path("extract").asText(null);

            if (extract == null || extract.isEmpty()) {
                String redirectedTitle = fetchRedirectedTitle(formattedCountry);

                if (redirectedTitle != null && !redirectedTitle.equalsIgnoreCase(country)) {

                    return getCountrySummary(redirectedTitle);
                } else {
                    return new WikipediaSummary(title, "Conteúdo não disponível.");
                }
            }

            return new WikipediaSummary(title, extract);

        } catch (Exception e) {
            System.err.println("[8] Erro ao obter dados da Wikipedia: " + e.getMessage());
            return new WikipediaSummary("Erro", "Não foi possível obter informações.");
        }
    }

    public String fetchRedirectedTitle(String formattedCountry) {
        URI uri = URI.create(
                "https://en.wikipedia.org/w/api.php?action=query&prop=revisions&rvslots=main&rvprop=content&titles="
                        + formattedCountry + "&format=json");
        try {
            String jsonResponse = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode pages = root.path("query").path("pages");

            if (!pages.fieldNames().hasNext()) {
                return null;
            }

            String pageId = pages.fieldNames().next();
            JsonNode revisions = pages.path(pageId).path("revisions");

            if (revisions.isArray() && revisions.size() > 0) {
                String content = revisions.get(0).path("slots").path("main").path("*").asText("");
                return extractRedirectedTitle(content);
            }
        } catch (Exception e) {
            System.err.println("[11] Erro ao obter redirecionamento: " + e.getMessage());
        }
        return null;
    }

    private String extractRedirectedTitle(String content) {
        if (content.startsWith("#REDIRECT [[")) {
            int start = content.indexOf("[[") + 2;
            int end = content.indexOf("]]");
            if (start > 1 && end > start) {
                return content.substring(start, end);
            }
        }
        return null;
    }
}
