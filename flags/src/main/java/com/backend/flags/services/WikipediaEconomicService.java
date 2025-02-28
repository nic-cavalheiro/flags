// package com.backend.flags.services;

// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.databind.SerializationFeature;

// import com.backend.flags.services.WikipediaService;
// import com.backend.flags.models.WikiEconomicsInfo;

// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// import java.net.URI;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;

// @Service
// public class WikipediaEconomicService {

//     private final RestTemplate restTemplate;
//     private final ObjectMapper objectMapper;
//     private final WikipediaService wikipediaService;

//     private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext=true&titles=";

//     //private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exsectionformat=raw&titles=";

//     public WikipediaEconomicService(RestTemplate restTemplate, ObjectMapper objectMapper) {
//         this.restTemplate = restTemplate;
//         this.objectMapper = objectMapper;
//         this.wikipediaService = wikipediaService;
//     }

//     public WikiEconomicsInfo getCountryEconomicInfo(String country) {
//         String formattedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8).replace("+", "%20");
//         URI uri = URI.create(WIKIPEDIA_API_URL + formattedCountry + "&format=json");

//         System.out.println("[0] URL de api/wikipedia/{country}/economics montada: " + uri);

//         try {
//             String jsonResponse = restTemplate.getForObject(uri, String.class);

//             // Criando o ObjectMapper para formatar o JSON
//             objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

//             // Convertendo a string JSON para um objeto e depois formatando
//             Object json = objectMapper.readValue(jsonResponse, Object.class);
//             String formattedJson = objectMapper.writeValueAsString(json);

//             System.out.println("[1] Resposta da api/wikipedia/{country}/economics:\n" + formattedJson);
            
//             JsonNode root = objectMapper.readTree(jsonResponse);
//             JsonNode pages = root.path("query").path("pages");

//             if (!pages.fieldNames().hasNext()) {
//                 System.out.println("[2] Nenhuma página encontrada.");
//                 return new WikiEconomicsInfo(country, "section", "Conteúdo não disponível.");
//             }

//             String pageId = pages.fieldNames().next();
//             JsonNode page = pages.path(pageId);
//             String title = page.path("title").asText("Título não disponível.");
//             String extract = page.path("extract").asText(null);

//             if (extract == null || extract.isEmpty()) {

//                 WikipediaService service = new WikipediaService();

//                 System.out.println("[5] Extract de getEconomicInfo vazio. Buscando redirecionamento...");
//                 String redirectedTitle = fetchRedirectedTitle(formattedCountry);

//                 if (redirectedTitle != null && !redirectedTitle.equalsIgnoreCase(country)) {
//                     System.out.println("[6] Redirecionando para: " + redirectedTitle);
                    
//                     return wikipediaService.getCountrySummary(redirectedTitle);

                
        
//             } else {
//                 return new WikiEconomicsInfo(title, "section", "Conteúdo econômico não disponível.");
//             }

//             // Aqui tentamos encontrar a parte de economia ou política no texto
//             String economicContent = extractEconomicSection(extract);

//             return new WikiEconomicsInfo(title, "section", economicContent);
//         } catch (Exception e) {
//             System.err.println("[3] Erro ao obter dados econômicos da Wikipedia: " + e.getMessage());
//             return new WikiEconomicsInfo("Erro", "Não foi possível obter informações.", "Erro ao buscar dados.");
//         }
//     }

//     private String extractEconomicSection(String text) {
//         String[] sections = text.split("\n");
//         StringBuilder economicContent = new StringBuilder();
//         boolean isEconomicSection = false;

//         for (String line : sections) {

//             if (line.toUpperCase().contains("Economy") || line.toUpperCase().contains("Politics")) {
//                 isEconomicSection = true;

//             } else if (isEconomicSection && line.matches("^[A-Z][a-z]+.*")) {
//                 break; 
//             }

//             if (isEconomicSection) {
//                 economicContent.append(line).append("\n");
//             }
//         }
//         return economicContent.toString().trim().isEmpty() ? "Informação econômica não encontrada." : economicContent.toString().trim();
//     }
// }
package com.backend.flags.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
    private final WikipediaService wikipediaService;

    private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext=true&titles=";

    public WikipediaEconomicService(RestTemplate restTemplate, ObjectMapper objectMapper, WikipediaService wikipediaService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.wikipediaService = wikipediaService;
    }

    public WikiEconomicsInfo getCountryEconomicInfo(String country) {
        String formattedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8).replace("+", "%20");
        URI uri = URI.create(WIKIPEDIA_API_URL + formattedCountry + "&format=json");

        System.out.println("[0] URL de api/wikipedia/{country}/economics montada: " + uri);

        try {
            String jsonResponse = restTemplate.getForObject(uri, String.class);

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
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
                System.out.println("[5] Extract de getEconomicInfo vazio. Buscando redirecionamento...");
                String redirectedTitle = wikipediaService.fetchRedirectedTitle(formattedCountry);

                if (redirectedTitle != null && !redirectedTitle.equalsIgnoreCase(country)) {
                    System.out.println("[6] Redirecionando para: " + redirectedTitle);
                    return this.getCountryEconomicInfo(redirectedTitle);  // Usando `this` em vez de `wikipediaEconomicService`
                } else {
                    System.out.println("[7] Nenhum redirecionamento encontrado.");
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
        String[] sections = text.split("\n");
        StringBuilder economicContent = new StringBuilder();
        boolean isEconomicSection = false;

        for (String line : sections) {
            if (line.toUpperCase().contains("ECONOMY") || line.toUpperCase().contains("POLITICS")) {
                isEconomicSection = true;
            } else if (isEconomicSection && line.matches("^[A-Z][a-z]+.*")) {
                break;
            }

            if (isEconomicSection) {
                economicContent.append(line).append("\n");
            }
        }
        return economicContent.toString().trim().isEmpty() ? "Informação econômica não encontrada." : economicContent.toString().trim();
    }
}



