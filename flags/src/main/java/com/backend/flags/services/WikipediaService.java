package com.backend.flags.services;

import com.backend.flags.models.WikipediaSummary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URI;

@Service
public class WikipediaService {

    private final RestTemplate restTemplate;
    private static final String WIKIPEDIA_API_URL = "https://en.wikipedia.org/api/rest_v1/page/summary/";

    public WikipediaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WikipediaSummary getCountrySummary(String country) {
        String formattedCountry = country.replace(" ", "_");
        URI uri = URI.create(WIKIPEDIA_API_URL + formattedCountry);

                // Imprimir a URL montada para depuração
                System.out.println("URL montada: " + uri);

        try {
            return restTemplate.getForObject(uri, WikipediaSummary.class);
        } catch (Exception e) {
            // Lidar com exceções
            return new WikipediaSummary("Erro", "Não foi possível obter informações.");
        }
    }
}

