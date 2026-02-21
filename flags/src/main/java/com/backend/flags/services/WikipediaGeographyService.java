package com.backend.flags.services;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backend.flags.models.WikiGeographyInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// AI generated
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class WikipediaGeographyService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final WikipediaService wikipediaService;

    // AI generated integration for Wikipedia API Geography parsing
    private static final String WIKIPEDIA_PAGE_IMAGES_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=images&imlimit=50&titles=";
    private static final String WIKIPEDIA_IMAGE_INFO_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=imageinfo&iiprop=url&titles=";

    // AI generated: In-memory cache to prevent Wikipedia API overload
    private final Map<String, WikiGeographyInfo> cache = new ConcurrentHashMap<>();

    public WikipediaGeographyService(RestTemplate restTemplate, ObjectMapper objectMapper,
            WikipediaService wikipediaService) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.wikipediaService = wikipediaService;
    }

    public WikiGeographyInfo getCountryGeographyInfo(String country) {
        // AI generated: check cache first
        if (cache.containsKey(country)) {
            return cache.get(country);
        }

        String formattedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8).replace("+", "%20");
        String countryIdOrRedirect = country;
        try {
            // Check redirect primarily for finding correct Wikidata Entity Name
            String redirectedTitle = wikipediaService.fetchRedirectedTitle(formattedCountry);
            if (redirectedTitle != null) {
                countryIdOrRedirect = redirectedTitle;
                formattedCountry = URLEncoder.encode(redirectedTitle, StandardCharsets.UTF_8).replace("+", "%20");
            }
        } catch (Exception e) {
        }

        URI uri = URI.create(WIKIPEDIA_PAGE_IMAGES_URL + formattedCountry + "&format=json");

        try {
            // [AI Generated] Direct Wikidata Query for precise Map Locator P242
            String preciseMapUrl = fetchPreciseMapFromWikidata(countryIdOrRedirect);

            String jsonResponse = restTemplate.getForObject(uri, String.class);
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            JsonNode root = objectMapper.readTree(jsonResponse);
            JsonNode pages = root.path("query").path("pages");

            if (!pages.fieldNames().hasNext()) {
                return updateCacheAndReturn(country, new WikiGeographyInfo(country, "section", preciseMapUrl, null));
            }

            String pageId = pages.fieldNames().next();
            JsonNode page = pages.path(pageId);
            String title = page.path("title").asText("Título não disponível.");
            JsonNode images = page.path("images");

            // AI generated: search for map and globe images
            String mapImageTitle = null;
            String globeImageTitle = null;
            String fallbackImageTitle = null;

            if (!images.isMissingNode() && images.isArray()) {
                for (JsonNode image : images) {
                    String imgTitle = image.path("title").asText();
                    String lowerTitle = imgTitle.toLowerCase();

                    if (lowerTitle.contains("orthographic") || lowerTitle.contains("on_the_globe")) {
                        globeImageTitle = imgTitle;
                    } else if (lowerTitle.contains("map") || lowerTitle.contains("location")) {
                        mapImageTitle = imgTitle;
                    } else if (fallbackImageTitle == null && !lowerTitle.contains("icon")
                            && !lowerTitle.contains("flag") && !lowerTitle.contains("emblem")
                            && !lowerTitle.endsWith(".svg")) {
                        // Keep a valid photo as fallback just in case there is no topology map
                        fallbackImageTitle = imgTitle;
                    }
                }
            }

            if (mapImageTitle == null) {
                mapImageTitle = fallbackImageTitle;
            }
            if (globeImageTitle == null) {
                globeImageTitle = mapImageTitle; // fallback globe to map if no globe found
            }

            if (mapImageTitle == null) {
                String redirectedTitle = wikipediaService.fetchRedirectedTitle(formattedCountry);

                if (redirectedTitle != null && !redirectedTitle.equalsIgnoreCase(country)) {
                    return this.getCountryGeographyInfo(redirectedTitle);
                } else {
                    return updateCacheAndReturn(country, new WikiGeographyInfo(title, "section", null, null));
                }
            }

            // Fetch actual URL for the map and globe images
            String globeUrl = resolveImageUrl(globeImageTitle);
            String mapUrl = preciseMapUrl;

            if (mapUrl == null && mapImageTitle != null) {
                mapUrl = resolveImageUrl(mapImageTitle); // fallback to wikipedia guessed map
            }

            if (mapUrl != null || globeUrl != null) {
                return updateCacheAndReturn(country, new WikiGeographyInfo(title, "section", mapUrl, globeUrl));
            }

            return updateCacheAndReturn(country, new WikiGeographyInfo(title, "section", null, null));

        } catch (Exception e) {
            System.err.println("[3] Erro ao obter dados geográficos da Wikipedia: " + e.getMessage());
            return new WikiGeographyInfo("Erro", "Não foi possível obter informações geográficas.", null, null);
        }
    }

    private String resolveImageUrl(String imageTitle) {
        if (imageTitle == null)
            return null;
        try {
            String formattedImageTitle = URLEncoder.encode(imageTitle, StandardCharsets.UTF_8).replace("+", "%20");
            URI imageUri = URI.create(WIKIPEDIA_IMAGE_INFO_URL + formattedImageTitle + "&format=json");
            String imageJsonResponse = restTemplate.getForObject(imageUri, String.class);
            JsonNode imageRoot = objectMapper.readTree(imageJsonResponse);
            JsonNode imagePages = imageRoot.path("query").path("pages");

            if (imagePages.fieldNames().hasNext()) {
                String imgPageId = imagePages.fieldNames().next();
                JsonNode imgInfoArray = imagePages.path(imgPageId).path("imageinfo");
                if (!imgInfoArray.isMissingNode() && imgInfoArray.isArray() && imgInfoArray.size() > 0) {
                    return imgInfoArray.get(0).path("url").asText(null);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao resolver URL da imagem: " + imageTitle);
        }
        return null;
    }

    // AI generated: Helper method to save to cache and return
    private WikiGeographyInfo updateCacheAndReturn(String key, WikiGeographyInfo result) {
        cache.put(key, result);
        return result;
    }

    private String fetchPreciseMapFromWikidata(String countryName) {
        try {
            // Find Entity ID first
            String searchUrl = "https://www.wikidata.org/w/api.php?action=wbsearchentities&search="
                    + URLEncoder.encode(countryName, StandardCharsets.UTF_8).replace("+", "%20")
                    + "&language=en&format=json";

            String searchResponse = restTemplate.getForObject(URI.create(searchUrl), String.class);
            JsonNode searchRoot = objectMapper.readTree(searchResponse);
            JsonNode searchResults = searchRoot.path("search");

            if (searchResults.isArray() && searchResults.size() > 0) {
                String countryId = searchResults.get(0).path("id").asText();

                String sparqlQuery = "SELECT ?map WHERE { wd:" + countryId + " wdt:P242 ?map . } LIMIT 1";
                String encodedQuery = URLEncoder.encode(sparqlQuery, StandardCharsets.UTF_8).replace("+", "%20");

                org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
                headers.set("User-Agent", "CoinConversor/1.0 (https://github.com/nic-cavalheiro)");
                headers.set("Accept", "application/sparql-results+json");
                org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

                org.springframework.http.ResponseEntity<String> sparqlResponse = restTemplate.exchange(
                        URI.create("https://query.wikidata.org/sparql?query=" + encodedQuery + "&format=json"),
                        org.springframework.http.HttpMethod.GET,
                        entity,
                        String.class);

                JsonNode sparqlRoot = objectMapper.readTree(sparqlResponse.getBody());
                JsonNode bindings = sparqlRoot.path("results").path("bindings");

                if (bindings.isArray() && bindings.size() > 0) {
                    return bindings.get(0).path("map").path("value").asText();
                }
            }
        } catch (Exception e) {
            System.err.println("Erro buscando mapa preciso na Wikidata: " + e.getMessage());
        }
        return null;
    }
}
