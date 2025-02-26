package com.backend.flags.controllers;

import com.backend.flags.models.WikiEconomicsInfo;
import com.backend.flags.services.WikipediaEconomicService;
import com.backend.flags.services.WikipediaService;
import org.springframework.web.bind.annotation.*;
import com.backend.flags.models.WikipediaSummary; 

@RestController
@RequestMapping("/api/wikipedia")
@CrossOrigin(origins = "*")
public class WikipediaController {

    private final WikipediaService wikipediaService;
    private final WikipediaEconomicService wikipediaEconomicService;  // Declaração do serviço de economia

    public WikipediaController(WikipediaService wikipediaService, WikipediaEconomicService wikipediaEconomicService) {
        this.wikipediaService = wikipediaService;
        this.wikipediaEconomicService = wikipediaEconomicService;  // Injeção do serviço de economia
    }

    @GetMapping("/{country}")
    public WikipediaSummary getCountryInfo(@PathVariable String country) {
        return wikipediaService.getCountrySummary(country);
    }

    @GetMapping("/{country}/economics")
    public WikiEconomicsInfo getCountryEconomicInfo(@PathVariable String country) {
        return wikipediaEconomicService.getCountryEconomicInfo(country);
    }
}