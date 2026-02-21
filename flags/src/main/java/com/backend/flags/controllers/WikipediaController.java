package com.backend.flags.controllers;

import com.backend.flags.models.WikiEconomicsInfo;
import com.backend.flags.models.WikiGeographyInfo;
import com.backend.flags.models.ArtMetadata;
import com.backend.flags.services.WikipediaEconomicService;
import com.backend.flags.services.WikipediaGeographyService;
import com.backend.flags.services.WikipediaArtService;
import com.backend.flags.services.WikipediaService;
import org.springframework.web.bind.annotation.*;
import com.backend.flags.models.WikipediaSummary;

@RestController
@RequestMapping("/api/wikipedia")
@CrossOrigin(origins = "*")
public class WikipediaController {

    private final WikipediaService wikipediaService;
    private final WikipediaEconomicService wikipediaEconomicService; // Declaração do serviço de economia

    // AI generated integration
    private final WikipediaGeographyService wikipediaGeographyService;
    private final WikipediaArtService wikipediaArtService;

    public WikipediaController(WikipediaService wikipediaService, WikipediaEconomicService wikipediaEconomicService,
            WikipediaGeographyService wikipediaGeographyService, WikipediaArtService wikipediaArtService) {
        this.wikipediaService = wikipediaService;
        this.wikipediaEconomicService = wikipediaEconomicService; // Injeção do serviço de economia
        this.wikipediaGeographyService = wikipediaGeographyService; // AI generated
        this.wikipediaArtService = wikipediaArtService; // AI generated
    }

    @GetMapping("/{country}")
    public WikipediaSummary getCountryInfo(@PathVariable String country) {
        return wikipediaService.getCountrySummary(country);
    }

    @GetMapping("/{country}/economics")
    public WikiEconomicsInfo getCountryEconomicInfo(@PathVariable String country) {
        return wikipediaEconomicService.getCountryEconomicInfo(country);
    }

    // AI generated endpoints
    @GetMapping("/{country}/geography")
    public WikiGeographyInfo getCountryGeographyInfo(@PathVariable String country) {
        return wikipediaGeographyService.getCountryGeographyInfo(country);
    }

    @GetMapping("/{country}/art")
    public ArtMetadata getCountryArtInfo(@PathVariable String country) {
        return wikipediaArtService.getCountryArtInfo(country);
    }
}