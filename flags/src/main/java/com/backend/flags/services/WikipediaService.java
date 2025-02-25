
/*package com.backend.flags.services;

import java.util.regex.*;
import com.backend.flags.models.WikipediaSummary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.Map;

@Service
public class WikipediaService {

    private final RestTemplate restTemplate;
    private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/rest.php/v1/page/";

    public WikipediaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WikipediaSummary getCountrySummary(String country) {
        String formattedCountry = country.replace(" ", "_");
        URI uri = URI.create(WIKIPEDIA_API_URL + formattedCountry); // Obtém wikitext

        System.out.println("URL montada: " + uri);

        try {
            // A API retorna um JSON, então usamos um Map para capturar `source`
            Map<?, ?> response = restTemplate.getForObject(uri, Map.class);
            String source = response != null ? (String) response.get("source") : "Não disponível";

            // Extrai a descrição curta do wikitext
            String shortDescription = extractSubTitle(source, "short description");

            return new WikipediaSummary(formattedCountry, source, shortDescription);

        } catch (Exception e) {
            return new WikipediaSummary("Erro", "Não foi possível obter informações.", "Não disponível");
        }
    }

    public static String extractSubTitle(String source, String key) {
        if (source == null || source.isEmpty()) {
            return "Nenhum conteúdo disponível.";
        }

        // Expressão regular para capturar {{key|conteúdo}}
        String regex = "\\{\\{" + key + "\\|(.*?)}}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);

        if (matcher.find()) {
            return matcher.group(1).trim(); // Retorna o conteúdo dentro das chaves
        }

        return "Não encontrado";
    }
}*/

package com.backend.flags.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.backend.flags.models.WikipediaSummary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

@Service
public class WikipediaService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=extracts&exintro=true&explaintext=true&titles=";

    public WikipediaService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public WikipediaSummary getCountrySummary(String country) {
        // Substitui espaços por "_" no nome do país para compatibilidade com a URL da Wikipédia
        String formattedCountry = country.replace(" ", "_");
        URI uri = URI.create(WIKIPEDIA_API_URL + formattedCountry + "&format=json");

        System.out.println("URL montada: " + uri); // Debug

        try {
            // Faz a requisição HTTP e obtém a resposta como String
            String jsonResponse = restTemplate.getForObject(uri, String.class);
            // Converte a resposta JSON para um objeto JsonNode
            JsonNode root = objectMapper.readTree(jsonResponse);

            // Acessa os dados da resposta JSON
            JsonNode pages = root.path("query").path("pages");

            // Obtém o ID da primeira página (isso funciona para artigos que existem)
            String pageId = pages.fieldNames().next();
            JsonNode page = pages.path(pageId);
            String extract = page.path("extract").asText("Descrição não disponível."); // Valor padrão se "extract" não for encontrado

            // Retorna o objeto WikipediaSummary com o título do país e o resumo
            return new WikipediaSummary(country, extract);
        } catch (Exception e) {
            // Caso haja algum erro, retorna uma resposta padrão indicando erro
            return new WikipediaSummary("Erro", "Não foi possível obter informações.");
        }
    }
}




