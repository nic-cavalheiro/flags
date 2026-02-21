package com.backend.flags.services;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.backend.flags.models.ArtMetadata;
import com.backend.flags.models.Artwork;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WikipediaArtService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final WikipediaService wikipediaService;

    private static final String USER_AGENT = "CoinConversor/1.0 (https://github.com/nic-cavalheiro)";
    private final Map<String, ArtMetadata> cache = new ConcurrentHashMap<>();

    public WikipediaArtService(RestTemplate restTemplate, ObjectMapper objectMapper,
            WikipediaService wikipediaService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.wikipediaService = wikipediaService;
    }

    public ArtMetadata getCountryArtInfo(String country) {
        System.out.println("\n[WikiArt] === Iniciando busca de arte para: " + country + " ===");

        if (cache.containsKey(country)) {
            System.out.println("[WikiArt] [SUCESSO] Dados recuperados do cache para: " + country);
            return cache.get(country);
        }

        try {
            System.out.println("[WikiArt] 1. Buscando ID do país na Wikidata...");
            String countryId = fetchWikidataCountryId(country);

            if (countryId == null) {
                System.out.println(
                        "[WikiArt] [AVISO] ID não encontrado para '" + country + "'. Tentando redirecionamento...");
                String redirectedTitle = wikipediaService.fetchRedirectedTitle(country);

                if (redirectedTitle != null && !redirectedTitle.equalsIgnoreCase(country)) {
                    System.out.println("[WikiArt] Redirecionando de '" + country + "' para '" + redirectedTitle + "'");
                    return this.getCountryArtInfo(redirectedTitle);
                }

                System.out.println("[WikiArt] [FALHA] Nenhum ID ou redirecionamento válido para: " + country);
                return updateCacheAndReturn(country, new ArtMetadata(country, new ArrayList<>()));
            }

            System.out.println("[WikiArt] [SUCESSO] ID encontrado: " + countryId + ". Executando SPARQL...");
            List<Artwork> artworks = fetchHighQualityArtworks(countryId);

            if (artworks.isEmpty()) {
                System.out.println(
                        "[WikiArt] [AVISO] A query retornou com sucesso, mas nenhuma obra passou nos filtros de qualidade para: "
                                + country);
            } else {
                System.out.println("[WikiArt] [SUCESSO] Finalizado. " + artworks.size()
                        + " obras de alta qualidade retornadas para: " + country);
            }

            return updateCacheAndReturn(country, new ArtMetadata(country, artworks));

        } catch (Exception e) {
            System.err.println(
                    "[WikiArt] [ERRO CRÍTICO] Falha ao processar dados de '" + country + "': " + e.getMessage());
            e.printStackTrace();
            return new ArtMetadata(country, new ArrayList<>());
        }
    }

    private String fetchWikidataCountryId(String country) throws Exception {
        URI searchUri = UriComponentsBuilder.fromHttpUrl("https://www.wikidata.org/w/api.php")
                .queryParam("action", "wbsearchentities")
                .queryParam("search", country)
                .queryParam("language", "en")
                .queryParam("format", "json")
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(searchUri, HttpMethod.GET, entity, String.class);

        JsonNode searchResults = objectMapper.readTree(response.getBody()).path("search");
        if (searchResults.isArray() && !searchResults.isEmpty()) {
            return searchResults.get(0).path("id").asText();
        }
        return null;
    }

    private List<Artwork> fetchHighQualityArtworks(String countryId) throws Exception {
        // Query blindada: Exige tipos específicos de arte e vincula o autor fortemente
        // ao país.
        String sparqlQuery = """
                SELECT DISTINCT ?artworkLabel ?authorLabel ?date ?styleLabel ?periodLabel ?cityLabel ?image WHERE {
                  {
                    SELECT DISTINCT ?artwork ?image ?author ?date WHERE {
                      BIND(wd:%1$s AS ?country)

                      # Filtro estrito: Pintura (Q3305213), Escultura (Q860861), Desenho (Q93184), Afresco (Q134194)
                      VALUES ?artType { wd:Q3305213 wd:Q860861 wd:Q93184 wd:Q134194 }

                      {
                        ?artwork wdt:P170 ?author .
                        ?author wdt:P27 ?country .
                      } UNION {
                        ?artwork wdt:P495 ?country .
                        ?artwork wdt:P170 ?author .
                      }

                      ?artwork wdt:P31/wdt:P279* ?artType .
                      ?artwork wdt:P18 ?image .
                      ?artwork wdt:P571|wdt:P580 ?date .
                    } LIMIT 75
                  }
                  OPTIONAL { ?artwork wdt:P135 ?style . }
                  OPTIONAL { ?artwork wdt:P2348 ?period . }
                  OPTIONAL { ?artwork wdt:P276 ?city . }
                  SERVICE wikibase:label { bd:serviceParam wikibase:language "pt,en,[AUTO_LANGUAGE]". }
                }
                """.formatted(countryId);

        URI sparqlUri = UriComponentsBuilder.fromHttpUrl("https://query.wikidata.org/sparql")
                .queryParam("query", sparqlQuery)
                .queryParam("format", "json")
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT);
        headers.set(HttpHeaders.ACCEPT, "application/sparql-results+json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(sparqlUri, HttpMethod.GET, entity, String.class);
        JsonNode bindings = objectMapper.readTree(response.getBody()).path("results").path("bindings");

        List<Artwork> artworks = new ArrayList<>();
        if (bindings.isArray()) {
            System.out
                    .println("[WikiArt] Extraindo e validando " + bindings.size() + " resultados brutos do SPARQL...");

            for (JsonNode binding : bindings) {
                Artwork artwork = new Artwork();

                String title = extractValue(binding, "artworkLabel");
                String author = extractValue(binding, "authorLabel");

                // Se o rótulo voltar como um ID da Wikidata (Ex: Q12345), nós limpamos
                if (title == null || title.matches("^Q\\d+$"))
                    title = "Obra Desconhecida";
                if (author == null || author.matches("^Q\\d+$"))
                    author = "Autor Desconhecido";

                artwork.setTitle(title);
                artwork.setAuthor(author);

                String dateStr = extractValue(binding, "date");
                if (dateStr != null && dateStr.length() >= 4) {
                    artwork.setDate(dateStr.substring(0, 4));
                }

                artwork.setStyle(extractValue(binding, "styleLabel", "Estilo não especificado"));
                artwork.setHistoricalPeriod(extractValue(binding, "periodLabel", "Período não especificado"));
                artwork.setCity(extractValue(binding, "cityLabel", "Local não especificado"));
                artwork.setImageUrl(extractValue(binding, "image"));

                // Filtrar duplicatas pela URL da imagem
                boolean isDuplicate = artworks.stream()
                        .anyMatch(a -> a.getImageUrl() != null && a.getImageUrl().equals(artwork.getImageUrl()));

                if (!isDuplicate && artwork.getImageUrl() != null) {
                    artworks.add(artwork);
                }

                // Limite expandido para o retorno da API
                if (artworks.size() >= 100)
                    break;
            }
        }
        return artworks;
    }

    // Método auxiliar para deixar o código de extração do JSON mais limpo e seguro
    private String extractValue(JsonNode binding, String key) {
        return extractValue(binding, key, null);
    }

    private String extractValue(JsonNode binding, String key, String defaultValue) {
        JsonNode node = binding.path(key).path("value");
        return node.isMissingNode() || node.isNull() ? defaultValue : node.asText();
    }

    private ArtMetadata updateCacheAndReturn(String key, ArtMetadata result) {
        cache.put(key, result);
        return result;
    }
}